package com.miro.widgets.infrastructure.repository;

import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.dto.Pagination;
import com.miro.widgets.domain.entity.Widget;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

        assertEquals(3, filter.collectList().blockOptional().orElse(Collections.emptyList()).size());
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

        assertEquals(0, filter.collectList().blockOptional().orElse(Collections.emptyList()).size());
    }

    @Test
    public void Given_PaginationProcess_Then_ReturnCorrectAmountOfValuesForEachPage() {

        InMemoryWidgetRepository repository = new InMemoryWidgetRepository();

        Widget widget;
        for (int i = 1; i <= 100; i++) {
            widget = Widget.builder().id(UUID.randomUUID()).zindex(i).width(i).height(i).coordinate(Coordinate.builder().x(i).z(i).build()).build();
            repository.create(widget).block();
        }

        Pagination pagination;
        List<Widget> widgets;
        for (int page = 1; page <= 10; page++) {

            pagination = new Pagination(page, 10);
            widgets = repository.getAll(pagination).collectList().blockOptional().orElse(Collections.emptyList());
            assertEquals(10, widgets.size());

            for (int item = 1; item <= 10; item++) {
                assertEquals((pagination.skip() + item), widgets.get(item - 1).getZindex());
            }
        }
    }

    @Test
    public void Given_PaginationProcess_WithNotRoundedNumberOfItems_Then_NotBreakIfAsForMoreThenExists() {

        InMemoryWidgetRepository repository = new InMemoryWidgetRepository();

        Widget widget;
        for (int i = 1; i <= 15; i++) {
            widget = Widget.builder().id(UUID.randomUUID()).zindex(i).width(i).height(i).coordinate(Coordinate.builder().x(i).z(i).build()).build();
            repository.create(widget).block();
        }

        Pagination pagination;
        List<Widget> widgets;
        for (int page = 1; page <= 2; page++) {

            pagination = new Pagination(page, 10);
            widgets = repository.getAll(pagination).collectList().blockOptional().orElse(Collections.emptyList());

            if(page == 1) {
                assertEquals(10, widgets.size());
            }

            if(page == 2) {
                assertEquals(5, widgets.size());
            }
        }

    }
}