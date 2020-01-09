package com.miro.widgets.api;

import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class WidgetController implements WidgetControllerApi {

    private final WidgetService service;

    @Override
    public Flux<WidgetResponse> allWidgets() {
        return service.getAll();
    }

    @Override
    public Mono<WidgetResponse> getWidget(UUID widgetId) {
        return service.get(widgetId);
    }

    @Override
    public Mono<WidgetResponse> createWidget(WidgetRequest body) {
        return service.createWidget(body);
    }

    @Override
    public Mono<WidgetResponse> updateWidget(UUID widgetId, WidgetRequest body) {
        return service.updateWidget(widgetId, body);
    }

    @Override
    public Mono<Void> deleteWidget(UUID widgetId) {
        return service.deleteWidget(widgetId);
    }
}
