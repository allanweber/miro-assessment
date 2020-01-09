package com.miro.widgets.domain.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ViolationDto {

    private final String fieldName;

    private final String message;
}
