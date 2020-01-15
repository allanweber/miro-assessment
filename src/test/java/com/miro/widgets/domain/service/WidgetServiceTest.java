package com.miro.widgets.domain.service;

import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.dto.Pagination;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.entity.Widget;
import com.miro.widgets.domain.mapper.WidgetMapper;
import com.miro.widgets.domain.repository.WidgetRepository;
import com.miro.widgets.infrastructure.configuration.ExecutorConfiguration;
import org.junit.jupiter.api.Assertions;
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
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class WidgetServiceTest {

    private WidgetMapper mapper = Mappers.getMapper(WidgetMapper.class);

    @Mock
    private WidgetRepository repository;

    @Mock
    private ExecutorConfiguration executorConfiguration;

    @InjectMocks
    private WidgetService service;

    @BeforeEach
    private void setup() {
        ReflectionTestUtils.setField(service, "mapper", mapper);
    }

    @Test
    public void Given_WidgetWithIndexNull_Then_SetZIndexEqualToMaxPlusOne(){
        Coordinate coordinate = Coordinate.builder().x(1).z(1).build();
        WidgetRequest request = WidgetRequest.builder().coordinate(coordinate).height(3).width(3).build();
        Widget createdEntity = Widget.builder().id(UUID.randomUUID()).coordinate(coordinate).height(3).width(3).zindex(3).build();

        Mockito.when(repository.filter(any())).thenReturn(Flux.empty());

        Mockito.when(repository.getMaxIndex()).thenReturn(Mono.justOrEmpty(10));

        Mockito.when(repository.create(any())).thenReturn(Mono.justOrEmpty(createdEntity));

        Mono<WidgetResponse> widgetResponseMono = service.createWidget(request);

        StepVerifier.create(widgetResponseMono)
                .expectNext(mapper.fromEntity(createdEntity))
                .verifyComplete();

        Mockito.verify(repository).getMaxIndex();
    }

    @Test
    public void Given_WidgetWithIndex_Then_NeverExecuteGetMaxZIndex(){
        Coordinate coordinate = Coordinate.builder().x(1).z(1).build();
        WidgetRequest request = WidgetRequest.builder().coordinate(coordinate).zindex(2).height(3).width(3).build();
        Widget createdEntity = Widget.builder().id(UUID.randomUUID()).coordinate(coordinate).height(3).width(3).zindex(3).build();

        Mockito.when(repository.filter(any())).thenReturn(Flux.empty());

        Mockito.when(repository.getMaxIndex()).thenReturn(Mono.justOrEmpty(10));

        Mockito.when(repository.create(any())).thenReturn(Mono.justOrEmpty(createdEntity));

        Mockito.when(executorConfiguration.getThreads()).thenReturn(1);

        Mono<WidgetResponse> widgetResponseMono = service.createWidget(request);

        StepVerifier.create(widgetResponseMono)
                .expectNext(mapper.fromEntity(createdEntity))
                .verifyComplete();

        Mockito.verify(repository, Mockito.never()).getMaxIndex();
    }

    @Test
    public void Given_NullPagination_Then_ThrowException() {
        Assertions.assertThrows(NullPointerException.class, () -> service.getAll(null), "Pagination must be informed.");
    }
}