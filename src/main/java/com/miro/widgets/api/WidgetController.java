package com.miro.widgets.api;

import com.miro.widgets.domain.dto.Pagination;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class WidgetController implements WidgetControllerApi {

    private final WidgetService service;

    @Override
    public ResponseEntity<List<WidgetResponse>> allWidgets(Integer page, Integer count) {

        Pagination pagination = new Pagination(page, count);
        return service.getAll(pagination).collectList().blockOptional()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(Collections.emptyList()));
    }

    @Override
    public ResponseEntity<WidgetResponse> getWidget(UUID widgetId) {
        return service.get(widgetId).blockOptional()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @Override
    public ResponseEntity<WidgetResponse> createWidget(WidgetRequest body) {
        return service.createWidget(body).blockOptional()
                .map(widget -> ResponseEntity.created(URI.create(String.format("/widget/%s", widget.getId()))).body(widget))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<WidgetResponse> updateWidget(UUID widgetId, WidgetRequest body) {
        return service.updateWidget(widgetId, body).blockOptional()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<?> deleteWidget(UUID widgetId) {
        return service.deleteWidget(widgetId).blockOptional()
                .map(any -> ResponseEntity.status(HttpStatus.GONE).build())
                .orElse(ResponseEntity.notFound().build());
    }
}
