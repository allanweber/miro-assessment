package com.miro.widgets.domain.repository;

import com.miro.widgets.domain.entity.Widget;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface WidgetRepository {

    Flux<Widget> getAll();

    Mono<Widget> get(UUID id);

    Mono<Widget> create(Widget widget);

    Mono<Widget> update(UUID id, Widget widget);

    Mono<Void> delete(UUID id);
}
