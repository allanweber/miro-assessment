package com.miro.widgets.domain.repository;

import com.miro.widgets.domain.entity.Widget;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Primary
@Repository
public class InMemoryWidgetRepository implements WidgetRepository {

    private final ConcurrentHashMap<UUID, Widget> records = new ConcurrentHashMap<>();

    @Override
    public Flux<Widget> getAll() {
        return Flux.fromStream(records.values().stream());
    }

    @Override
    public Mono<Widget> get(UUID id) {
        return Mono.justOrEmpty(
                records.get(id)
        );
    }

    @Override
    public Mono<Widget> create(Widget widget) {
        records.put(widget.getId(), widget);
        return Mono.just(widget);
    }

    @Override
    public Mono<Widget> update(UUID id, Widget widget) {
        records.replace(id, widget);
        return Mono.just(widget);
    }

    @Override
    public Mono<Void> delete(UUID id) {
        records.remove(id);
        return Mono.empty();
    }

    @Override
    public Mono<Integer> getMaxZIndex() {
        return Mono.just(records.values().stream().max(Comparator.comparing(Widget::getZindex)).map(Widget::getZindex).orElse(-1));
    }
}
