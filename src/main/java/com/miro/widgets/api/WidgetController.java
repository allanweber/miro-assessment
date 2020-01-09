package com.miro.widgets.api;

import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class WidgetController implements WidgetControllerApi {

    @Override
    public Flux<WidgetResponse> allWidgets() {
        return null;
    }

    @Override
    public Mono<WidgetResponse> getWidget(UUID widgetId) {
        return null;
    }

    @Override
    public Mono<WidgetResponse> createWidget(WidgetRequest body) {
        return Mono.justOrEmpty(new WidgetResponse(UUID.randomUUID(), new Coordinate(), 0, 0, 0));
    }

    @Override
    public Mono<WidgetResponse> updateWidget(UUID widgetId, WidgetRequest body) {
        return null;
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteWidget(UUID widgetId) {
        return null;
    }
}
