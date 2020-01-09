package com.miro.widgets.domain.mapper;

import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.entity.Widget;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface WidgetMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    Widget fromCreateRequest(WidgetRequest request);

    @Mapping(target = "id", source = "id")
    Widget fromUpdateRequest(UUID id, WidgetRequest request);

    WidgetResponse fromEntity(Widget widget);
}
