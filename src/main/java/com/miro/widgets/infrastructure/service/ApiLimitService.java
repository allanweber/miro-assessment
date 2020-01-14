package com.miro.widgets.infrastructure.service;

import com.miro.widgets.infrastructure.dto.ApiLimit;
import com.miro.widgets.infrastructure.dto.RequestLimit;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ApiLimitService {

    private static final Map<RequestLimit, ApiLimiter> LIMITS = new ConcurrentHashMap<>();

    public ApiLimiter getOrCreateApiLimiter(RequestLimit key, ApiLimit attributes) {
        synchronized (key.getLabel().intern()) {
            ApiLimiter apiLimiter;

            if (LIMITS.containsKey(key)) {
                apiLimiter = LIMITS.get(key);
                if (LocalDateTime.now().isAfter(apiLimiter.getNextReset())) {
                    apiLimiter.release();
                    LIMITS.remove(key);
                    apiLimiter = create(key, attributes);
                }
            } else {
                apiLimiter = create(key, attributes);
            }

            return apiLimiter;
        }
    }

    private ApiLimiter create(RequestLimit key, ApiLimit attributes) {
        ApiLimiter apiLimiter;
        apiLimiter = ApiLimiter.create(attributes.getLimit(), attributes.getTime());
        LIMITS.put(key, apiLimiter);
        return apiLimiter;
    }
}
