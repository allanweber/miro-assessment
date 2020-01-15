package com.miro.widgets.api;

import com.miro.widgets.infrastructure.dto.RequestLimit;
import com.miro.widgets.infrastructure.repository.ApiLimitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
class LimitControllerTest {

    @Mock
    private ApiLimitRepository apiLimitRepository;

    @InjectMocks
    private LimitController controller;

    @Test
    public void Given_RequestLimit_Max_Seconds_Then_UpdateLimit(){

        Mockito.doNothing().when(apiLimitRepository).updateLimitSemaphore(any(), any());

        ResponseEntity<?> response = controller.updateLimit(RequestLimit.GET, 1,1L);

        assertEquals(200, response.getStatusCodeValue());

    }

}