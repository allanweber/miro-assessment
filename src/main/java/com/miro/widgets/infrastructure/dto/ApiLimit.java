package com.miro.widgets.infrastructure.dto;

import lombok.Data;

import java.time.Duration;

@Data
public class ApiLimit {

    private Integer limit;

    private Duration time;
}
