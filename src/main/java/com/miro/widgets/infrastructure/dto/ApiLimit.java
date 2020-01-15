package com.miro.widgets.infrastructure.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Duration;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApiLimit {

    @NotNull
    private Integer limit;

    @NotNull
    private Duration time;
}
