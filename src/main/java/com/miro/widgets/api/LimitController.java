package com.miro.widgets.api;

import com.miro.widgets.infrastructure.dto.ApiLimit;
import com.miro.widgets.infrastructure.dto.RequestLimit;
import com.miro.widgets.infrastructure.repository.ApiLimitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RequiredArgsConstructor
@RestController
public class LimitController implements LimitControllerApi {

    private final ApiLimitRepository apiLimitRepository;

    @Override
    public ResponseEntity<?> updateLimit(RequestLimit limitKey, Integer max, Long seconds) {
        ApiLimit apiLimit = new ApiLimit(max, Duration.ofSeconds(seconds));
        apiLimitRepository.updateLimitSemaphore(limitKey, apiLimit);
        return ResponseEntity.ok().build();
    }
}
