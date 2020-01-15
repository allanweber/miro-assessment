package com.miro.widgets.infrastructure.configuration;

import com.miro.widgets.infrastructure.handlers.ApiLimitInterceptor;
import com.miro.widgets.infrastructure.repository.ApiLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ApiLimitConfiguration configuration;

    private final ApiLimitRepository limitService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(ApiLimitInterceptor.create(configuration, limitService));
    }
}
