package com.miro.widgets.domain.service;

import com.miro.widgets.domain.dto.request.WidgetRequest;
import com.miro.widgets.domain.dto.response.WidgetResponse;
import com.miro.widgets.domain.entity.Widget;
import com.miro.widgets.domain.mapper.WidgetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class WidgetService {

    private final WidgetMapper mapper;

    public Mono<WidgetResponse> createWidget(WidgetRequest widget){
        Widget entity = mapper.fromCreateRequest(widget);

        WidgetResponse response = mapper.fromEntity(entity);

        return Mono.just(response);
    }
}
