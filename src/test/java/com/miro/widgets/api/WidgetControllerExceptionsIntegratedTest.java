package com.miro.widgets.api;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.ResponseErrorDto;
import com.miro.widgets.domain.helper.ObjectMapperProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WidgetControllerExceptionsIntegratedTest {

    private static final String WIDGET_PATH = "/widget";
    private static final String WIDGET_PATH_WITH_ID = String.format("%s/{widgetId}", WIDGET_PATH);

    private final ObjectWriter widgetRequestWriter = ObjectMapperProvider.get().writerFor(WidgetRequest.class);
    private final ObjectReader responseErrorDtoReader = ObjectMapperProvider.get().readerFor(ResponseErrorDto.class);

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void Given_RequestWithCoordinateNull_Then_ReturnBadRequestWithErrorResponse() throws Exception {

        WidgetRequest request = WidgetRequest.builder().height(1).width(1).build();

        String body = widgetRequestWriter.writeValueAsString(request);

        String jsonResponse = mockMvc.perform(post(WIDGET_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertEquals("coordinate", getFiledName(jsonResponse));
    }

    @Test
    public void Given_RequestWithHeightNull_Then_ReturnBadRequestWithErrorResponse() throws Exception {

        Coordinate coordinate = Coordinate.builder().z(1).x(1).build();
        WidgetRequest request = WidgetRequest.builder().coordinate(coordinate).width(1).build();

        String body = widgetRequestWriter.writeValueAsString(request);

        String jsonResponse = mockMvc.perform(post(WIDGET_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertEquals("height", getFiledName(jsonResponse));
    }

    @Test
    public void Given_RequestWithWidthNull_Then_ReturnBadRequestWithErrorResponse() throws Exception {

        Coordinate coordinate = Coordinate.builder().z(1).x(1).build();
        WidgetRequest request = WidgetRequest.builder().coordinate(coordinate).height(1).build();

        String body = widgetRequestWriter.writeValueAsString(request);

        String jsonResponse = mockMvc.perform(post(WIDGET_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        assertEquals("width", getFiledName(jsonResponse));
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private String getFiledName(String jsonResponse) {

        ResponseErrorDto responseErrorDto = responseErrorDtoReader.readValue(jsonResponse);

        return ((Map<String,String>)((ArrayList)responseErrorDto.getDetail()).get(0)).get("fieldName");

    }
}
