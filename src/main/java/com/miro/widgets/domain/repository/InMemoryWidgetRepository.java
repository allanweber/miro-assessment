package com.miro.widgets.domain.repository;

import com.miro.widgets.domain.entity.Widget;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Primary
@Repository
public class InMemoryWidgetRepository implements WidgetRepository {

    private final ConcurrentHashMap<UUID, Widget> records = new ConcurrentHashMap<>();

    @Override
    public Flux<Widget> getAll() {
        return Flux.fromStream(records.values().stream().map(Widget::new).collect(Collectors.toList()).stream());
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
    public Mono<Void> delete(UUID id) {
        return Mono.fromSupplier(() -> records.remove(id)).then();
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
