package com.miro.widgets.infrastructure.handlers;

import com.miro.widgets.infrastructure.configuration.ApiLimitConfiguration;
import com.miro.widgets.infrastructure.repository.ApiLimitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SpringApplicationReadyEventListener {
    private final ApiLimitConfiguration limitConfiguration;

    private final ApiLimitRepository limitService;

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() {

        log.info("Spring ApplicationReadyEvent. Creating limits configuration");
        try {
            limitService.createDefault(limitConfiguration);
        } catch (IllegalArgumentException ex) {
            log.error("Error to create default api limits", ex);
        }
    }
}
