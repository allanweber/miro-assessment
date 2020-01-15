package com.miro.widgets.infrastructure.repository;

import com.miro.widgets.infrastructure.configuration.ApiLimitConfiguration;
import com.miro.widgets.infrastructure.dto.ApiLimit;
import com.miro.widgets.infrastructure.dto.RequestLimit;
import com.miro.widgets.infrastructure.entity.ApiLimitSemaphore;
import com.miro.widgets.infrastructure.specification.SemaphoreExpired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Repository
@Slf4j
public class ApiLimitRepository {

    private static final Map<RequestLimit, ApiLimitSemaphore> LIMITS = new ConcurrentHashMap<>();

    public Optional<ApiLimitSemaphore> getSemaphore(RequestLimit key) {
        synchronized (key.getLabel().intern()) {
            ApiLimitSemaphore apiLimitSemaphore = null;

            if (LIMITS.containsKey(key)) {
                apiLimitSemaphore = get(key);
            } else {
                log.error("Api Limit Semaphore with key {} not found.", key);
            }

            return Optional.ofNullable(apiLimitSemaphore);
        }
    }

    public void createDefault(ApiLimitConfiguration limitConfiguration) {
        if(Objects.isNull(limitConfiguration)){
            throw new IllegalArgumentException("limitConfiguration");
        }
        if(Objects.isNull(limitConfiguration.getGeneralLimit())){
            throw new IllegalArgumentException("limitConfiguration.getGeneralLimit");
        }

        Stream.of(RequestLimit.values())
                .forEach(key -> {
                    LIMITS.remove(key);
                    create(key, limitConfiguration.getGeneralLimit());
                });
    }

    public void updateLimitSemaphore(RequestLimit key, @NotNull ApiLimit apiLimit) {
        if (LIMITS.containsKey(key)) {
            releaseLimit(key);
            LIMITS.remove(key);
        }
        create(key, apiLimit);
    }

    private void create(RequestLimit key, ApiLimit attributes) {
        ApiLimitSemaphore apiLimitSemaphore = ApiLimitSemaphore.create(attributes.getLimit(), attributes.getTime());
        LIMITS.put(key, apiLimitSemaphore);
    }

    private ApiLimitSemaphore get(RequestLimit key) {
        ApiLimitSemaphore apiLimitSemaphore;
        apiLimitSemaphore = LIMITS.get(key);
        if (SemaphoreExpired.satisfiedBy().test(apiLimitSemaphore)) {
            releaseLimit(key);
            apiLimitSemaphore = reuse(key);
        }
        return apiLimitSemaphore;
    }

    private ApiLimitSemaphore reuse(RequestLimit key) {
        ApiLimitSemaphore apiLimitSemaphore = LIMITS.get(key);
        apiLimitSemaphore = ApiLimitSemaphore.create(apiLimitSemaphore.getMaxPermits(), apiLimitSemaphore.getDuration());
        LIMITS.replace(key, apiLimitSemaphore);
        return apiLimitSemaphore;
    }

    private void releaseLimit(RequestLimit key) {
        ApiLimitSemaphore apiLimitSemaphore = LIMITS.get(key);
        apiLimitSemaphore.release();
    }
}
