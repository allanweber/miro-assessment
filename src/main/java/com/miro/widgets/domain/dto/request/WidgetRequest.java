package com.miro.widgets.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miro.widgets.domain.dto.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WidgetRequest {

    @NotNull
    private Coordinate coordinate;

    private Integer zindex;

    @NotNull
    private Integer width;

    @NotNull
    private Integer height;
}
