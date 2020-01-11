package com.miro.widgets.domain.entity;

import com.miro.widgets.domain.dto.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Widget {

    private UUID id;

    private Coordinate coordinate;

    private Integer zindex = 0;

    private Integer width;

    private Integer height;

    public Widget(Widget widget) {
        this.id = widget.id;
        this.coordinate = widget.coordinate;
        this.zindex = widget.zindex;
        this.width = widget.width;
        this.height = widget.height;
    }
}
