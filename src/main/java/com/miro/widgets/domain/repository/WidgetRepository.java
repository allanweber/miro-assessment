package com.miro.widgets.domain.repository;

import com.miro.widgets.domain.entity.Widget;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Predicate;

public interface WidgetRepository {

    Flux<Widget> getAll();

    Mono<Widget> get(UUID id);

    Mono<Widget> create(Widget widget);

    Mono<Widget> update(UUID id, Widget widget);

    Mono<Widget> delete(UUID id);

    Mono<Integer> getMaxIndex();

    Flux<Widget> filter(Predicate<Widget> predicate);
}
