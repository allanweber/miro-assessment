package com.miro.widgets.domain.repository;

import com.miro.widgets.domain.entity.Widget;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryWidgetRepositoryTest {

    @Test
    public void When_GettingMaxIndex_WithEmptyRecords_Then_ReturnMinusOne() {

        InMemoryWidgetRepository repository = new InMemoryWidgetRepository();

        Mono<Integer> mono = repository.getMaxIndex();

        StepVerifier.create(mono)
                .expectNext(-1)
                .verifyComplete();
    }

    @Test
    public void When_GettingMaxIndex_WithRecordsInserted_Then_ReturnTwo() {

        InMemoryWidgetRepository repository = new InMemoryWidgetRepository();

        repository.create(Widget.builder().id(UUID.randomUUID()).zindex(2).build()).block();

        Mono<Integer> mono = repository.getMaxIndex();

        StepVerifier.create(mono)
                .expectNext(2)
                .verifyComplete();
    }

    @Test
    public void When_FilterByPredicate_Then_ReturnExpectedValues() {
        InMemoryWidgetRepository repository = new InMemoryWidgetRepository();

        List<Widget> widgets = Arrays.asList(
                Widget.builder().id(UUID.randomUUID()).zindex(2).build(),
                Widget.builder().id(UUID.randomUUID()).zindex(5).build(),
                Widget.builder().id(UUID.randomUUID()).zindex(10).build(),
                Widget.builder().id(UUID.randomUUID()).zindex(12).build(),
                Widget.builder().id(UUID.randomUUID()).zindex(20).build()
        );

        widgets.forEach(widget -> repository.create(widget).block());

        Predicate<Widget> predicate = (widget) -> widget.getZindex() >= 10;
        Flux<Widget> filter = repository.filter(predicate);

        assertEquals(3, Optional.ofNullable(filter.collectList().block()).orElse(Collections.emptyList()).size());
    }

    @Test
    public void When_FilterByPredicate_Then_ReturnEmpty() {
        InMemoryWidgetRepository repository = new InMemoryWidgetRepository();

        List<Widget> widgets = Collections.singletonList(
                Widget.builder().id(UUID.randomUUID()).zindex(2).build()
        );

        widgets.forEach(widget -> repository.create(widget).block());

        Predicate<Widget> predicate = (widget) -> widget.getZindex() >= 30;
        Flux<Widget> filter = repository.filter(predicate);

        assertEquals(0, Optional.ofNullable(filter.collectList().block()).orElse(Collections.emptyList()).size());
    }
}