package com.miro.widgets.domain.mapper;

import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.entity.Widget;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {com.miro.widgets.domain.mapper.WidgetMapperImpl.class})
class WidgetMapperTest {

    @Autowired
    private WidgetMapper mapper;

    @Test
    public void Given_WidgetRequest_When_Creating_Then_MapToEntityWithNewId(){

        Coordinate coordinate = new Coordinate(1, 2);
        WidgetRequest request = new WidgetRequest(coordinate, 1,2,3);

        Widget widget = mapper.fromCreateRequest(request);

        assertNotNull(widget.getId());
        assertEquals(coordinate, widget.getCoordinate());
        assertEquals(1, widget.getZindex());
        assertEquals(2, widget.getWidth());
        assertEquals(3, widget.getHeight());
    }

    @Test
    public void Given_WidgetRequest_When_Updating_Then_MapToEntityWithExpectedId(){

        Coordinate coordinate = new Coordinate(1, 2);
        WidgetRequest request = new WidgetRequest( coordinate, 1,2,3);

        UUID id = UUID.randomUUID();
        Widget expectedEntity = new Widget(id, coordinate, 1,2,3);

        Widget widget = mapper.fromUpdateRequest(id, request);

        assertEquals(expectedEntity, widget);
    }

    @Test
    public void Given_WidgetEntity_When_MappingFromEntity_Then_MapToResponse(){

        UUID id = UUID.randomUUID();
        Coordinate coordinate = new Coordinate(1, 2);
        Widget entity = new Widget(id, coordinate, 1,2,3);

        WidgetResponse expectedResponse = new WidgetResponse(id, coordinate,1,2,3);

        WidgetResponse response = mapper.fromEntity(entity);

        assertEquals(expectedResponse, response);
    }
}