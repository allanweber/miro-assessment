package com.miro.widgets.infrastructure.handlers;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.miro.widgets.domain.dto.response.ResponseErrorDto;
import com.miro.widgets.domain.helper.ObjectMapperProvider;
import com.miro.widgets.infrastructure.configuration.ApiLimitConfiguration;
import com.miro.widgets.infrastructure.dto.RequestLimit;
import com.miro.widgets.infrastructure.entity.ApiLimitSemaphore;
import com.miro.widgets.infrastructure.repository.ApiLimitRepository;
import com.miro.widgets.infrastructure.specification.HttpRequestSpecification;
import com.miro.widgets.infrastructure.specification.LimitConfigurationEnabledSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@SuppressWarnings("PMD.OnlyOneReturn")
public final class ApiLimitInterceptor extends HandlerInterceptorAdapter {

    private final ApiLimitConfiguration limitConfiguration;

    private final ApiLimitRepository limitService;

    private final ObjectWriter responseErrorDtoWriter = ObjectMapperProvider.get().writerFor(ResponseErrorDto.class);

    public static ApiLimitInterceptor create(ApiLimitConfiguration configuration, ApiLimitRepository limitService) {
        return new ApiLimitInterceptor(configuration, limitService);
    }

    private ApiLimitInterceptor(ApiLimitConfiguration limitConfiguration, ApiLimitRepository limitService) {
        super();
        this.limitConfiguration = limitConfiguration;
        this.limitService = limitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (LimitConfigurationEnabledSpecification.satisfiedBy().negate().test(limitConfiguration)) {
            return true;
        }

        Optional<RequestLimit> key = getConfigKey(request);
        if (key.isEmpty()) {
            return true;
        }

        Optional<ApiLimitSemaphore> limitSemaphore = limitService.getSemaphore(key.get());

        if(limitSemaphore.isEmpty()){
            return true;
        }

        boolean allowRequest = limitSemaphore.get().tryAcquire();

        String nextWindow = limitSemaphore.get().getNextReset().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        response.addHeader("limit", limitSemaphore.get().getMaxPermits().toString());
        response.addHeader("available", limitSemaphore.get().getRequestsAvailable().toString());
        response.addHeader("next-reset", nextWindow);

        if (!allowRequest) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            String content = responseErrorDtoWriter.writeValueAsString(
                    new ResponseErrorDto(String.format("Too many request for this resource, wait until %s to try again", nextWindow))
            );
            response.setContentType("application/json");
            response.setContentLength(content.length());
            response.getWriter().write(content);
        }

        return allowRequest;
    }


    private Optional<RequestLimit> getConfigKey(HttpServletRequest request) {

        if (HttpRequestSpecification.widgetRequest().test(request)) {
            if (HttpRequestSpecification.dataManipulationMethod().test(request)) {
                return Optional.of(RequestLimit.fromLabel(request.getMethod().toUpperCase(Locale.getDefault())));

            } else if (HttpRequestSpecification.httpGetMethod().test(request)) {
                if (HttpRequestSpecification.httpGetByIdMethod().test(request)) {
                    return Optional.of(RequestLimit.GET);
                } else {
                    return Optional.of(RequestLimit.GET_ALL);
                }
            }
        }
        return Optional.empty();
    }
}
