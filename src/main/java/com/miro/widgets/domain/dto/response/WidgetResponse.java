package com.miro.widgets.domain.dto.response;

import com.miro.widgets.domain.dto.Coordinate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class WidgetResponse {

    private UUID id;

    private Coordinate coordinate;

    private Integer zindex;

    private Integer width;

    private Integer height;

    private String lastModification;
}
