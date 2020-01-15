package com.miro.widgets.infrastructure.specification;

import com.miro.widgets.infrastructure.configuration.ApiLimitConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.function.Predicate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LimitConfigurationEnabledSpecification {

    public static Predicate<ApiLimitConfiguration> satisfiedBy(){
        return configuration -> Objects.nonNull(configuration) && Objects.nonNull(configuration.getGeneralLimit()) && configuration.getEnabled();
    }
}
