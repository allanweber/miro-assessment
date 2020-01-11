package com.miro.widgets.domain.service;

import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.entity.Widget;
import com.miro.widgets.domain.exception.NotFoundException;
import com.miro.widgets.domain.mapper.WidgetMapper;
import com.miro.widgets.domain.repository.WidgetRepository;
import com.miro.widgets.domain.specification.NotIndexSpecified;
import com.miro.widgets.domain.specification.UpdateWidgetConsistency;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

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

    public Mono<WidgetResponse> createWidget(WidgetRequest widget) {
        Widget entity = mapper.fromCreateRequest(widget);

        if (Objects.nonNull(widget.getZindex())) {
            pushWidgetsUpwards(widget.getZindex());
        }

        return getCorrectIndex(entity).map(index -> {
            entity.setZindex(index);
            return entity;
        }).flatMap(this::createAndMap);
    }

    private void pushWidgetsUpwards(final Integer index) {

        Integer currentIndex = index;
        Optional<Widget> widget;
        List<Widget> widgetsToUpdate = new ArrayList<>();
        do {
            widget = getByIndex(currentIndex);
            if (widget.isPresent()) {
                Widget record = widget.get();
                record.setZindex(record.getZindex() + 1);
                widgetsToUpdate.add(record);
            }
            currentIndex++;
        } while (widget.isPresent());

        widgetsToUpdate.forEach(record -> repository.update(record.getId(), record).block());
    }

    private Optional<Widget> getByIndex(Integer index) {
        Predicate<Widget> forwardSpace = (widget) -> widget.getZindex().equals(index);
        return repository.filter(forwardSpace).singleOrEmpty().blockOptional();
    }

    private Mono<WidgetResponse> createAndMap(Widget entity) {
        return repository.create(entity)
                .map(mapper::fromEntity);
    }

    private Mono<Integer> getCorrectIndex(Widget entity) {
        Mono<Integer> returnedMono;
        if (NotIndexSpecified.satisfiedBy().test(entity)) {
            returnedMono = repository.getMaxIndex().map(index -> index + 1);
        } else {
            returnedMono = Mono.justOrEmpty(entity.getZindex());
        }
        return returnedMono;
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
