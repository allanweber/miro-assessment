package com.miro.widgets.api;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.miro.widgets.ObjectMapperProvider;
import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WidgetControllerTest {

    private final ObjectWriter widgetRequestWriter = ObjectMapperProvider.get().writerFor(WidgetRequest.class);

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void When_HealthCheckEndpointIsCalled_Then_HealthStatusUp() throws Exception {

        WidgetRequest widgetRequest = new WidgetRequest(new Coordinate(1, 2), 1, 2, 3);

        String body = widgetRequestWriter.writeValueAsString(widgetRequest);

        WidgetResponse response = (WidgetResponse) mockMvc.perform(post("/widget")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn().getAsyncResult();

        assertNotNull(response.getId());
    }
}