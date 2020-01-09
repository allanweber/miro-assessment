package com.miro.widgets.api;

import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@Api(tags = "Widgets")
@RequestMapping("/widgets")
public interface WidgetControllerApi {

    @ApiOperation(notes = "Return a collection of Widgets sorted by z-index", value = "Return all Widgets", response = WidgetResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Widgets returned"),
            @ApiResponse(code = 400, message = "Something is wrong with your request")})
    @GetMapping
    Flux<WidgetResponse> allWidgets();

    @ApiOperation(notes = "Return a Widget based on ID", value = "Return a Widget", response = WidgetResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Widget returned"),
            @ApiResponse(code = 400, message = "Something is wrong with your request"),
            @ApiResponse(code = 404, message = "Could not find the widget")})
    @GetMapping("{widgetId}")
    Mono<WidgetResponse> getWidget(@ApiParam(name= "widgetId", value = "Widget id in UUID format", required = true) @PathVariable UUID widgetId);

    @ApiOperation(notes = "Create a new widget based", value = "Create new Widget", response = WidgetResponse.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Widgets created"),
            @ApiResponse(code = 400, message = "Something is wrong with your request")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    Mono<WidgetResponse> createWidget(@Valid @RequestBody WidgetRequest body);

    @ApiOperation(notes = "Update the Widget based on ID", value = "Update a Widget", response = WidgetResponse.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Widgets updated"),
            @ApiResponse(code = 400, message = "Something is wrong with your request"),
            @ApiResponse(code = 404, message = "Could not find the widget")})
    @PutMapping("/{widgetId}")
    Mono<WidgetResponse> updateWidget(@ApiParam(name= "widgetId", value = "Widget id in UUID format", required = true) @PathVariable UUID widgetId,
                               @Valid @RequestBody WidgetRequest body);

    @ApiOperation(notes = "Delete the Widget based on ID", value = "Delete a Widget", response = Object.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Widgets deleted"),
            @ApiResponse(code = 400, message = "Something is wrong with your request"),
            @ApiResponse(code = 404, message = "Could not find the widget")})
    @DeleteMapping("{widgetId}")
    Mono<ResponseEntity<Void>> deleteWidget(@ApiParam(name= "widgetId", value = "Widget id in UUID format", required = true) @PathVariable UUID widgetId);
}
