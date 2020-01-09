package com.miro.widgets.api;

import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@RestController
public class WidgetController implements WidgetControllerApi {

    @Override
    public Mono<String> allWidgets() {
        return null;
    }

    @Override
    public Mono<String> getWidget(@NotBlank UUID widgetId) {
        return null;
    }

    @Override
    public Mono<String> createWidgets(@Valid Object body) {
        return null;
    }

    @Override
    public Mono<String> createWidgets(@NotBlank UUID widgetId, @Valid Object body) {
        return null;
    }

    @Override
    public Mono<String> createWidgets(@NotBlank UUID widgetId) {
        return null;
    }
}
