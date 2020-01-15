package com.miro.widgets.infrastructure.specification;

import com.miro.widgets.infrastructure.entity.ApiLimitSemaphore;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SemaphoreExpired {

    public static Predicate<ApiLimitSemaphore> satisfiedBy(){
        return apiLimitSemaphore -> LocalDateTime.now().isAfter(apiLimitSemaphore.getNextReset());
    }
}
