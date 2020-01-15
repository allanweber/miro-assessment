package com.miro.widgets.domain.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;

import com.miro.widgets.domain.entity.Widget;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class WidgetServiceIntegratedTest {

    @Autowired
    WidgetService service;

    @AfterEach
    public void removeWidgets() {
        List<WidgetResponse> allWidgets = getAllWidgets();

        allWidgets.forEach(widget -> service.deleteWidget(widget.getId()).block());
    }

    private List<WidgetResponse> getAllWidgets() {
        return service.getAll().collectList().blockOptional().orElse(Collections.emptyList());
    }

    @Test
    public void Given_NewWidget_With_IndexEqualExistentRecord_Then_PushUpwardsOneRecord() throws InterruptedException {

        List<WidgetRequest> widgets = Arrays.asList(
                WidgetRequest.builder().zindex(5).build(),
                WidgetRequest.builder().zindex(10).build(),
                WidgetRequest.builder().zindex(12).build()
        );

        widgets.forEach(widget -> service.createWidget(widget).block());

        List<WidgetResponse> allWidgets = getAllWidgets();
        UUID idPreviousIndex10 = allWidgets.get(1).getId();

        WidgetResponse newWidgetAdded = service.createWidget(WidgetRequest.builder().zindex(10).build()).blockOptional().orElse(new WidgetResponse());

        Thread.sleep(500);

        allWidgets = getAllWidgets();

        assertEquals(5, allWidgets.get(0).getZindex());

        assertEquals(10, allWidgets.get(1).getZindex());
        assertEquals(newWidgetAdded.getId(), allWidgets.get(1).getId());

        assertEquals(11, allWidgets.get(2).getZindex());
        assertEquals(idPreviousIndex10, allWidgets.get(2).getId());

        assertEquals(12, allWidgets.get(3).getZindex());
    }

    @Test
    public void Given_NewWidget_With_IndexEqualExistentRecord_Then_PushUpwardsAllRecordsInSequence() throws InterruptedException {

        List<WidgetRequest> widgets = Arrays.asList(
                WidgetRequest.builder().zindex(5).build(),
                WidgetRequest.builder().zindex(10).build(),
                WidgetRequest.builder().zindex(11).build(),
                WidgetRequest.builder().zindex(12).build(),
                WidgetRequest.builder().zindex(13).build()
        );

        widgets.forEach(widget -> service.createWidget(widget).block());

        List<WidgetResponse> allWidgets = getAllWidgets();
        UUID idPreviousIndex10 = allWidgets.get(1).getId();
        UUID idPreviousIndex11 = allWidgets.get(2).getId();
        UUID idPreviousIndex12 = allWidgets.get(3).getId();
        UUID idPreviousIndex13 = allWidgets.get(4).getId();

        WidgetResponse newWidgetAdded = service.createWidget(WidgetRequest.builder().zindex(10).build()).blockOptional().orElse(new WidgetResponse());
        Thread.sleep(500);

        allWidgets = getAllWidgets();

        assertEquals(5, allWidgets.get(0).getZindex());

        assertEquals(10, allWidgets.get(1).getZindex());
        assertEquals(newWidgetAdded.getId(), allWidgets.get(1).getId());

        assertEquals(11, allWidgets.get(2).getZindex());
        assertEquals(idPreviousIndex10, allWidgets.get(2).getId());

        assertEquals(12, allWidgets.get(3).getZindex());
        assertEquals(idPreviousIndex11, allWidgets.get(3).getId());

        assertEquals(13, allWidgets.get(4).getZindex());
        assertEquals(idPreviousIndex12, allWidgets.get(4).getId());

        assertEquals(14, allWidgets.get(5).getZindex());
        assertEquals(idPreviousIndex13, allWidgets.get(5).getId());

    }

    @Test
    public void Given_RequestIndexNull_When_Update_Then_UseSavedIndex(){

        WidgetRequest request = WidgetRequest.builder().zindex(5).build();

        WidgetResponse widgetResponse = service.createWidget(request).blockOptional().orElse(WidgetResponse.builder().build());

        WidgetRequest updateRequest = WidgetRequest.builder().build();

        widgetResponse = service.updateWidget(widgetResponse.getId(), updateRequest).blockOptional().orElse(WidgetResponse.builder().build());

        assertEquals(5, widgetResponse.getZindex());
    }
}