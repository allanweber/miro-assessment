package com.miro.widgets.api;

import com.miro.widgets.infrastructure.configuration.ApiLimitConfiguration;
import com.miro.widgets.infrastructure.dto.ApiLimit;
import com.miro.widgets.infrastructure.dto.RequestLimit;
import com.miro.widgets.infrastructure.entity.ApiLimitSemaphore;
import com.miro.widgets.infrastructure.repository.ApiLimitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "api-limit.enabled=true")
public class ApiLimitsIntegratedTests {

    private ApiLimitConfiguration defaultLimitation = new ApiLimitConfiguration(true, new ApiLimit(2, Duration.ofHours(1)));

    private static final String WIDGET_PATH = "/widget";
    private static final String WIDGET_PATH_WITH_ID = String.format("%s/{widgetId}", WIDGET_PATH);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApiLimitRepository apiLimitRepository;

    @BeforeEach
    public void resetLimitation() {
        apiLimitRepository.createDefault(defaultLimitation);
    }

    @Test
    public void When_RequestGetAll_Then_ReturnApiLimitations() throws Exception {

        ApiLimitSemaphore semaphore = apiLimitRepository.getSemaphore(RequestLimit.GET_ALL).get();
        String nextReset = semaphore.getNextReset().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "1"))
                .andExpect(header().string("next-reset", nextReset));
    }

    @Test
    public void When_RequestGetAllUntilLimit_Then_ReturnApiLimitationsAndHttpStatus429() throws Exception {

        ApiLimitSemaphore semaphore = apiLimitRepository.getSemaphore(RequestLimit.GET_ALL).get();
        String nextReset = semaphore.getNextReset().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "1"))
                .andExpect(header().string("next-reset", nextReset));

        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "0"))
                .andExpect(header().string("next-reset", nextReset));

        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()))
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "0"))
                .andExpect(header().string("next-reset", nextReset));
    }

    @Test
    public void When_UpdateLimitsForGetAll_Then_ReturnApiWithNewLimitations() throws Exception {

        ApiLimitSemaphore semaphore = apiLimitRepository.getSemaphore(RequestLimit.GET_ALL).get();
        String nextReset = semaphore.getNextReset().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "1"))
                .andExpect(header().string("next-reset", nextReset));

        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "0"))
                .andExpect(header().string("next-reset", nextReset));


        ApiLimit newLimit = new ApiLimit(5, Duration.ofSeconds(100));
        apiLimitRepository.updateLimitSemaphore(RequestLimit.GET_ALL, newLimit);
        semaphore = apiLimitRepository.getSemaphore(RequestLimit.GET_ALL).get();
        nextReset = semaphore.getNextReset().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));


        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("limit", "5"))
                .andExpect(header().string("available", "4"))
                .andExpect(header().string("next-reset", nextReset));
    }

    @Test
    public void When_ConsumeTwoDifferentApi_Then_NotInterfereLimitationBetweenThen() throws Exception {
        ApiLimitSemaphore semaphoreGetAll = apiLimitRepository.getSemaphore(RequestLimit.GET_ALL).get();
        String nextResetGetAll = semaphoreGetAll.getNextReset().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        ApiLimitSemaphore semaphoreDelete = apiLimitRepository.getSemaphore(RequestLimit.GET_ALL).get();
        String nextResetDelete = semaphoreDelete.getNextReset().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "1"))
                .andExpect(header().string("next-reset", nextResetGetAll));

        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "0"))
                .andExpect(header().string("next-reset", nextResetGetAll));

        this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()))
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "0"))
                .andExpect(header().string("next-reset", nextResetGetAll));

        mockMvc.perform(MockMvcRequestBuilders
                .delete(WIDGET_PATH_WITH_ID, UUID.randomUUID().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().string("limit", "2"))
                .andExpect(header().string("available", "1"))
                .andExpect(header().string("next-reset", nextResetDelete));
    }
}
