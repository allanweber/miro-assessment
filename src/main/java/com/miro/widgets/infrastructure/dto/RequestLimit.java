package com.miro.widgets.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@AllArgsConstructor
public enum RequestLimit {
    GET_ALL("get-all"),
    GET("get"),
    PUT("put"),
    POST("post"),
    DELETE("delete");

    @Getter
    private String label;

    public static RequestLimit fromLabel(String label) {
        return RequestLimit.valueOf(label.toUpperCase(Locale.getDefault()));
    }
}
