package com.miro.widgets.infrastructure.configuration;

import com.miro.widgets.domain.repository.InMemoryWidgetRepository;
import com.miro.widgets.domain.repository.WidgetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RepositoryConfiguration {

    private final InMemoryWidgetRepository inMemoryWidgetRepository;

    @Bean
    public WidgetRepository getRepository(){
        return inMemoryWidgetRepository;
    }
}
