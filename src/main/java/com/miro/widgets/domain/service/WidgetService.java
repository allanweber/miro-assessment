package com.miro.widgets.domain.service;

import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.entity.Widget;
import com.miro.widgets.domain.exception.NotFoundException;
import com.miro.widgets.domain.mapper.WidgetMapper;
import com.miro.widgets.domain.repository.WidgetRepository;
import com.miro.widgets.domain.specification.UpdateWidgetConsistency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetMapper mapper;

    private final WidgetRepository repository;

    public Flux<WidgetResponse> getAll() {
        return repository.getAll().sort(Comparator.comparing(Widget::getZindex)).map(mapper::fromEntity);
    }

    public Mono<WidgetResponse> get(UUID id) {
        return repository.get(id).map(mapper::fromEntity)
                .switchIfEmpty(Mono.error(new NotFoundException(id)));
    }

    public Mono<WidgetResponse> createWidget(WidgetRequest widget) {
        Widget entity = mapper.fromCreateRequest(widget);

        return repository.create(entity)
                .map(mapper::fromEntity);
    }

    public Mono<WidgetResponse> updateWidget(UUID id, WidgetRequest widget) {
        Widget entity = mapper.fromUpdateRequest(id, widget);

        return repository.get(id)
                .switchIfEmpty(Mono.error(new NotFoundException(id)))
                .map(checkConsistency(entity))
                .then(repository.update(id, entity))
                .map(mapper::fromEntity);
    }

    public Mono<Void> deleteWidget(UUID id) {
        return repository.get(id)
                .switchIfEmpty(Mono.error(new NotFoundException(id)))
                .then(repository.delete(id));
    }

    private Function<Widget, Widget> checkConsistency(Widget entity) {
        return selected -> {
            if (UpdateWidgetConsistency.satisfiedBy().test(selected, entity)) {
                entity.setZindex(selected.getZindex());
            }
            return entity;
        };
    }
}
