package com.miro.widgets.infrastructure.configuration;

import com.miro.widgets.infrastructure.dto.ApiLimit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("api-limit")
@Component
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiLimitConfiguration {

    private Boolean enabled = false;

    private ApiLimit generalLimit;
}
