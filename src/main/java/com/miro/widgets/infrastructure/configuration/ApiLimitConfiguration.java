package com.miro.widgets.infrastructure.configuration;

import com.miro.widgets.infrastructure.dto.ApiLimit;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ConfigurationProperties("api-limit")
@Component
@NoArgsConstructor
@Data
public class ApiLimitConfiguration {

    private Boolean enabled = false;

    private ApiLimit generalLimit;

    private Map<String, ApiLimit> limits = new ConcurrentHashMap<>();
}
