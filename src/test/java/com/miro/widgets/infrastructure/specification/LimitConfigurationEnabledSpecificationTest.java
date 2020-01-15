package com.miro.widgets.infrastructure.specification;

import com.miro.widgets.infrastructure.configuration.ApiLimitConfiguration;
import com.miro.widgets.infrastructure.dto.ApiLimit;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimitConfigurationEnabledSpecificationTest {

    @Test
    public void Given_NullConfig_Then_ReturnFalse() {

        assertFalse(LimitConfigurationEnabledSpecification.satisfiedBy().test(null));
    }

    @Test
    public void Given_NullGeneralLimit_Then_ReturnFalse() {
        ApiLimitConfiguration limitConfiguration = new ApiLimitConfiguration(true, null);

        assertFalse(LimitConfigurationEnabledSpecification.satisfiedBy().test(limitConfiguration));
    }

    @Test
    public void Given_FalseEnable_Then_ReturnFalse() {
        ApiLimitConfiguration limitConfiguration = new ApiLimitConfiguration(false, new ApiLimit(1, Duration.ofSeconds(1)));

        assertFalse(LimitConfigurationEnabledSpecification.satisfiedBy().test(limitConfiguration));
    }

    @Test
    public void Given_TrueEnable_Then_ReturnTrue() {
        ApiLimitConfiguration limitConfiguration = new ApiLimitConfiguration(true, new ApiLimit(1, Duration.ofSeconds(1)));

        assertTrue(LimitConfigurationEnabledSpecification.satisfiedBy().test(limitConfiguration));
    }
}