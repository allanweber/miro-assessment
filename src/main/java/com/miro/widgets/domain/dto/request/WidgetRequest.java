package com.miro.widgets.domain.dto.request;

import com.miro.widgets.domain.dto.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.Valid;

@NoArgsConstructor
@AllArgsConstructor
@Valid
@Getter
public class WidgetRequest {

    @NonNull
    private Coordinate coordinate;

    private Integer zIndex;

    private Integer width;

    private Integer height;
}
