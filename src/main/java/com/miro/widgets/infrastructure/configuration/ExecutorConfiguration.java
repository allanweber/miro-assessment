package com.miro.widgets.infrastructure.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@ConfigurationProperties("executor")
@Component
@NoArgsConstructor
@Data
@Validated
public class ExecutorConfiguration {

    @NotNull
    private Integer threads;
}
