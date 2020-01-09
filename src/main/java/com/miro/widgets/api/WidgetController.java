package com.miro.widgets.api;

import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@RestController
public class WidgetController implements WidgetControllerApi {

    @Override
    public Flux<WidgetResponse> allWidgets() {
        return null;
    }

    @Override
    public Mono<WidgetResponse> getWidget(@NotBlank UUID widgetId) {
        return null;
    }

    @Override
    public Mono<WidgetResponse> createWidget(@Valid WidgetRequest body) {
        return null;
    }

    @Override
    public Mono<WidgetResponse> updateWidget(@NotBlank UUID widgetId, @Valid Object body) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteWidget(@NotBlank UUID widgetId) {
        return null;
    }
}
