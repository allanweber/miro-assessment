package com.miro.widgets.domain.service;

import com.miro.widgets.domain.dto.Pagination;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.entity.Widget;
import com.miro.widgets.domain.exception.NotFoundException;
import com.miro.widgets.domain.mapper.WidgetMapper;
import com.miro.widgets.domain.repository.WidgetRepository;
import com.miro.widgets.domain.specification.NotIndexSpecified;
import com.miro.widgets.domain.specification.UpdateWidgetConsistency;
import com.miro.widgets.infrastructure.configuration.ExecutorConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableConfigurationProperties(ExecutorConfiguration.class)
public class WidgetService {

    private final WidgetMapper mapper;

    private final WidgetRepository repository;

    private final ExecutorConfiguration executorConfiguration;

    public Flux<WidgetResponse> getAll(Pagination pagination) {
        Objects.requireNonNull(pagination, "Pagination must be informed.");
        return repository.getAll(pagination).map(mapper::fromEntity);
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
                .map(updateModification())
                .then(repository.update(id, entity))
                .map(mapper::fromEntity);
    }

    public Mono<WidgetResponse> deleteWidget(UUID id) {
        return repository.delete(id)
                .switchIfEmpty(Mono.error(new NotFoundException(id)))
                .map(mapper::fromEntity);
    }

    public Mono<WidgetResponse> createWidget(WidgetRequest widget) {
        Widget entity = mapper.fromCreateRequest(widget);

        if (Objects.nonNull(widget.getZindex())) {
            pushWidgetsUpwards(widget.getZindex(), entity.getId());
        }

        return getCorrectIndex(entity)
                .map(index -> {
                    updateModification().apply(entity);
                    entity.setZindex(index);
                    return entity;
                }).flatMap(this::createAndMap);
    }

    private Function<Widget, Widget> updateModification() {
        return widget -> {
            widget.setModification(LocalDateTime.now());
            return widget;
        };
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private void pushWidgetsUpwards(final Integer currentIndex, final UUID currentId) {

        ExecutorService executor = Executors.newFixedThreadPool(executorConfiguration.getThreads());
        CompletableFuture.runAsync(() -> {
            try {
                Integer index = currentIndex;
                UUID notThisId = currentId;
                Optional<Widget> widget;
                do {
                    widget = getByIndex(index, notThisId);
                    if (widget.isPresent()) {
                        Widget record = widget.get();
                        record.setZindex(record.getZindex() + 1);
                        repository.update(record.getId(), record).subscribe();
                        notThisId = record.getId();
                    }
                    index++;
                } while (widget.isPresent());
            } catch (Exception e) {
                log.error("Error to push widgets upwards.", e);
            }
        }, executor).thenRun(() -> log.info("Push widgets upwards finished."));
    }

    private Optional<Widget> getByIndex(Integer index, UUID currentId) {
        Predicate<Widget> forwardSpace = (widget) -> widget.getZindex().equals(index) && !widget.getId().equals(currentId);
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
