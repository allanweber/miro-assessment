package com.miro.widgets.domain.service;

import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.entity.Widget;
import com.miro.widgets.domain.mapper.WidgetMapper;
import com.miro.widgets.domain.repository.WidgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class WidgetServiceTest {

    private WidgetMapper mapper = Mappers.getMapper(WidgetMapper.class);

    @Mock
    private WidgetRepository repository;

    @InjectMocks
    private WidgetService service;

    @BeforeEach
    private void setup() {
        ReflectionTestUtils.setField(service, "mapper", mapper);
    }

    @Test
    void When_GettingAllWidgets_Then_ReturnResponseInTheRightOrder() {

        Coordinate coordinate = Coordinate.builder().x(1).z(1).build();

        List<Widget> widgets = Arrays.asList(
                Widget.builder().id(UUID.randomUUID()).coordinate(coordinate).height(3).width(3).zindex(3).build(),
                Widget.builder().id(UUID.randomUUID()).coordinate(coordinate).height(2).width(2).zindex(2).build(),
                Widget.builder().id(UUID.randomUUID()).coordinate(coordinate).height(1).width(1).zindex(1).build()
        );

        List<WidgetResponse> expectedResponses = widgets.stream().map(mapper::fromEntity).collect(Collectors.toList());

        Mockito.when(repository.getAll()).thenReturn(Flux.fromStream(widgets.stream()));

        Flux<WidgetResponse> fluxAll = service.getAll();

        StepVerifier.create(fluxAll)
                .expectNext(expectedResponses.get(2))
                .expectNext(expectedResponses.get(1))
                .expectNext(expectedResponses.get(0))
                .verifyComplete();
    }

    @Test
    public void Given_WidgetWithIndexNull_Then_SetZIndexEqualToMaxPlusOne(){
        Coordinate coordinate = Coordinate.builder().x(1).z(1).build();
        WidgetRequest request = WidgetRequest.builder().coordinate(coordinate).height(3).width(3).build();
        Widget createdEntity = Widget.builder().id(UUID.randomUUID()).coordinate(coordinate).height(3).width(3).zindex(3).build();

        Mockito.when(repository.getMaxZIndex()).thenReturn(Mono.justOrEmpty(10));

        Mockito.when(repository.create(any())).thenReturn(Mono.justOrEmpty(createdEntity));

        Mono<WidgetResponse> widgetResponseMono = service.createWidget(request);

        StepVerifier.create(widgetResponseMono)
                .expectNext(mapper.fromEntity(createdEntity))
                .verifyComplete();

        Mockito.verify(repository).getMaxZIndex();
    }

    @Test
    public void Given_WidgetWithIndex_Then_NeverExecuteGetMaxZIndex(){
        Coordinate coordinate = Coordinate.builder().x(1).z(1).build();
        WidgetRequest request = WidgetRequest.builder().coordinate(coordinate).zindex(2).height(3).width(3).build();
        Widget createdEntity = Widget.builder().id(UUID.randomUUID()).coordinate(coordinate).height(3).width(3).zindex(3).build();

        Mockito.when(repository.getMaxZIndex()).thenReturn(Mono.justOrEmpty(10));

        Mockito.when(repository.create(any())).thenReturn(Mono.justOrEmpty(createdEntity));

        Mono<WidgetResponse> widgetResponseMono = service.createWidget(request);

        StepVerifier.create(widgetResponseMono)
                .expectNext(mapper.fromEntity(createdEntity))
                .verifyComplete();

        Mockito.verify(repository, Mockito.never()).getMaxZIndex();
    }
}