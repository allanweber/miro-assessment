package com.miro.widgets.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.miro.widgets.domain.dto.Coordinate;
import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.helper.ObjectMapperProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WidgetControllerIntegratedTest {

    private static final String WIDGET_PATH = "/widget";
    private static final String WIDGET_PATH_WITH_ID = String.format("%s/{widgetId}", WIDGET_PATH);

    private final ObjectWriter widgetRequestWriter = ObjectMapperProvider.get().writerFor(WidgetRequest.class);
    private final ObjectReader widgetsResponseReader = ObjectMapperProvider.get().readerFor(new TypeReference<List<WidgetResponse>>() {});
    private final ObjectReader widgetResponseReader = ObjectMapperProvider.get().readerFor(WidgetResponse.class);

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void removeWidgets() {
        List<WidgetResponse> widgets = getAllWidgets();

        widgets.forEach(this::deleteWidget);
    }

    @Test
    public void When_GetAllWidgets_WithEmptyRecords_Then_ReturnEmptyResponse() {

        List<WidgetResponse> widgets = getAllWidgets();

        assertEquals(0, widgets.size());
    }

    @Test
    public void When_GetAllWidgets_Then_ReturnListResponse() {

        When_PostNewWidget_Then_ReturnExpectedResponse();

        List<WidgetResponse> widgets = getAllWidgets();

        assertEquals(1, widgets.size());
    }

    @SneakyThrows
    @Test
    public void When_PostNewWidget_Then_ReturnExpectedResponse() {

        WidgetRequest widgetRequest = new WidgetRequest(new Coordinate(1, 2), 1, 2, 3);

        WidgetResponse response = createWidget(widgetRequest);

        assertNotNull(response.getId());
    }

    @SneakyThrows
    @Test
    public void When_UpdateWidget_Then_ReturnUpdatedWidget() {

        WidgetRequest widgetRequest = new WidgetRequest(new Coordinate(1, 2), 1, 2, 3);

        WidgetResponse createResponse = createWidget(widgetRequest);

        WidgetRequest widgetPutRequest = new WidgetRequest(new Coordinate(100, 200), 1000, 200, 300);
        String body = widgetRequestWriter.writeValueAsString(widgetPutRequest);

        String jsonResponse =  mockMvc.perform(MockMvcRequestBuilders
                .put(WIDGET_PATH_WITH_ID, createResponse.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        WidgetResponse updateResponse = widgetResponseReader.readValue(jsonResponse);

        assertEquals(createResponse.getId(), updateResponse.getId());

        assertEquals(widgetPutRequest.getCoordinate(), updateResponse.getCoordinate());
        assertEquals(widgetPutRequest.getZindex(), updateResponse.getZindex());
        assertEquals(widgetPutRequest.getWidth(), updateResponse.getWidth());
        assertEquals(widgetPutRequest.getHeight(), updateResponse.getHeight());

        List<WidgetResponse> widgets = getAllWidgets();

        assertEquals(1, widgets.size());
    }

    @SneakyThrows
    @Test
    public void When_PutNonExistentWidget_Then_ReturnNotFound() {

        WidgetRequest widgetRequest = new WidgetRequest(new Coordinate(1, 2), 1, 2, 3);

        String body = widgetRequestWriter.writeValueAsString(widgetRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .put(WIDGET_PATH_WITH_ID, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void When_GettingWidget_Then_ReturnExpectedResponse() {
        WidgetRequest widgetRequest = new WidgetRequest(new Coordinate(1, 2), 1, 2, 3);

        WidgetResponse createResponse = createWidget(widgetRequest);

        String jsonResponse = mockMvc.perform(MockMvcRequestBuilders
                .get(WIDGET_PATH_WITH_ID, createResponse.getId()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        WidgetResponse getResponse = widgetResponseReader.readValue(jsonResponse);

        assertEquals(createResponse.getId(), getResponse.getId());
        assertEquals(createResponse.getCoordinate(), getResponse.getCoordinate());
        assertEquals(createResponse.getZindex(), getResponse.getZindex());
        assertEquals(createResponse.getWidth(), getResponse.getWidth());
        assertEquals(createResponse.getHeight(), getResponse.getHeight());
    }

    @SneakyThrows
    @Test
    public void When_GettingNonExistentWidget_Then_ReturnNotFound() {
        mockMvc.perform(MockMvcRequestBuilders
                .get(WIDGET_PATH_WITH_ID, UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void When_DeleteNonExistentWidget_Then_ReturnNotFound() {

        WidgetResponse widget = WidgetResponse.builder().id(UUID.randomUUID()).build();

        mockMvc.perform(MockMvcRequestBuilders
                .delete(WIDGET_PATH_WITH_ID, widget.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @SneakyThrows
    private List<WidgetResponse> getAllWidgets() {
        String jsonResponse = this.mockMvc.perform(get(WIDGET_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return widgetsResponseReader.readValue(jsonResponse);
    }

    private WidgetResponse createWidget(WidgetRequest widgetRequest) throws Exception {
        String body = widgetRequestWriter.writeValueAsString(widgetRequest);

        String jsonResponse = mockMvc.perform(post(WIDGET_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return widgetResponseReader.readValue(jsonResponse);
    }

    @SneakyThrows
    private void deleteWidget(WidgetResponse widget) {
        mockMvc.perform(MockMvcRequestBuilders
                .delete(WIDGET_PATH_WITH_ID, widget.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone())
                .andReturn();
    }
}