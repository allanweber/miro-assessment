package com.miro.widgets.domain.dto.response;

import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import lombok.Getter;
import lombok.NonNull;

import java.util.UUID;

@Getter
public class WidgetResponse extends WidgetRequest {

    private UUID id;

    public WidgetResponse(UUID id, @NonNull Coordinate coordinate, Integer zindex, Integer width, Integer height) {
        super(coordinate, zindex, width, height);
        this.id = id;
    }
}
