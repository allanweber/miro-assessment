package com.miro.widgets.domain.repository;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.miro.widgets.domain.dto.Pagination;
import com.miro.widgets.domain.entity.Widget;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Primary
@Repository
public class InMemoryWidgetRepository implements WidgetRepository {

    private final Map<UUID, Widget> records = new ConcurrentHashMap<>();

    @Override
    public Flux<Widget> getAll(Pagination pagination) {
        return Flux.fromStream(records.values().stream()
                .sorted(Comparator.comparing(Widget::getZindex))
                .skip(pagination.skip()).limit(pagination.getCount())
                .map(Widget::new).collect(Collectors.toList()).stream());
    }

    @Override
    public Mono<Widget> get(UUID id) {
        return Mono.fromSupplier(() -> {
            Widget widget = records.get(id);
            if (Objects.nonNull(widget)) {
                return new Widget(widget);
            } else {
                return null;
            }
        });
    }

    @Override
    public Mono<Widget> create(Widget widget) {
        return Mono.fromSupplier(() -> {
            records.put(widget.getId(), widget);
            return widget;
        });
    }

    @Override
    public Mono<Widget> update(UUID id, Widget widget) {
        return Mono.fromSupplier(() -> {
            records.replace(id, widget);
            return widget;
        });
    }

    @Override
    public Mono<Widget> delete(UUID id) {
        return Mono.justOrEmpty(records.remove(id));
    }

    @Override
    public Mono<Integer> getMaxIndex() {
        return Mono.just(records.values().stream().max(Comparator.comparing(Widget::getZindex)).map(Widget::getZindex).orElse(-1));
    }

    @Override
    public Flux<Widget> filter(Predicate<Widget> predicate) {
        return Flux.fromStream(records.values().stream().filter(predicate).map(Widget::new).collect(Collectors.toList()).stream());
    }
}
