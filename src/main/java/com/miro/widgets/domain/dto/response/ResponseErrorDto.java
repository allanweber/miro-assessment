package com.miro.widgets.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseErrorDto {

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object detail;

    public ResponseErrorDto(String message, Object detail) {
        this(message);
        this.detail = detail;
    }

    public ResponseErrorDto(String message) {
        this.message = message;
    }
}
