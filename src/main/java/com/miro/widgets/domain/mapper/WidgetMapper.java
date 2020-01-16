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
    @Mapping(target = "modification", ignore = true)
    Widget fromCreateRequest(WidgetRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "modification", ignore = true)
    Widget fromUpdateRequest(UUID id, WidgetRequest request);

    @Mapping(target = "lastModification", expression = "java(java.util.Optional.ofNullable(widget.getModification()).orElse(java.time.LocalDateTime.MIN).format(java.time.format.DateTimeFormatter.ofPattern(\"dd-MM-yyyy HH:mm:ss\")))")
    WidgetResponse fromEntity(Widget widget);
}
