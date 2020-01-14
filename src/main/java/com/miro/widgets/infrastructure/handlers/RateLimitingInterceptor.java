package com.miro.widgets.infrastructure.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@SuppressWarnings("PMD.OnlyOneReturn")
public class RateLimitingInterceptor extends HandlerInterceptorAdapter {

    private final Map<String, SimpleRateLimiter> limiters = new ConcurrentHashMap<>();

//    @Value("${rate.limit.enabled:true}")
//    private boolean enabled;

    @Value("${rate.limit.hourly.limit:2}")
    private int hourlyLimit;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
//        if (!enabled) {
//            return true;
//        }
        String clientId = request.getHeader("Client-Id");
        // let non-API requests pass
        if (clientId == null) {
            return true;
        }
        SimpleRateLimiter rateLimiter = getRateLimiter(clientId);
        boolean allowRequest = rateLimiter.tryAcquire();

        if (!allowRequest) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        }
        response.addHeader("X-RateLimit-Limit", String.valueOf(hourlyLimit));
        return allowRequest;
    }

    private SimpleRateLimiter getRateLimiter(String clientId) {
        if (limiters.containsKey(clientId)) {
            return limiters.get(clientId);
        } else {
            synchronized (clientId.intern()) {
                // double-checked locking to avoid multiple-reinitializations
                if (limiters.containsKey(clientId)) {
                    return limiters.get(clientId);
                }

                SimpleRateLimiter rateLimiter = SimpleRateLimiter.create(hourlyLimit, TimeUnit.HOURS);

                limiters.put(clientId, rateLimiter);
                return rateLimiter;
            }
        }
    }


}
