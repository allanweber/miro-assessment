package com.miro.widgets.infrastructure.service;

import com.miro.widgets.infrastructure.configuration.ApiLimitConfiguration;
import com.miro.widgets.infrastructure.dto.ApiLimit;
import com.miro.widgets.infrastructure.dto.RequestLimit;
import com.miro.widgets.infrastructure.entity.ApiLimitSemaphore;
import com.miro.widgets.infrastructure.repository.ApiLimitRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ApiLimitRepositoryTest {

    ApiLimitConfiguration limitConfiguration = new ApiLimitConfiguration (true, new ApiLimit(100, Duration.ofHours(1)));

    @Test
    void Given_SomeApiLimits_Then_TryToConsumeThen() {
        ApiLimitRepository apiLimitRepository = new ApiLimitRepository();

        apiLimitRepository.createDefault(limitConfiguration);

        ApiLimitSemaphore semaphore1 = apiLimitRepository.getSemaphore(RequestLimit.GET).get();
        ApiLimitSemaphore semaphore2 = apiLimitRepository.getSemaphore(RequestLimit.GET_ALL).get();

        assertEquals(100, semaphore1.getMaxPermits());
        assertEquals(100, semaphore1.getRequestsAvailable());

        assertEquals(100, semaphore2.getMaxPermits());
        assertEquals(100, semaphore2.getRequestsAvailable());

        assertTrue(semaphore1.tryAcquire());
        assertEquals(99, semaphore1.getRequestsAvailable());

        assertTrue(semaphore2.tryAcquire());
        assertTrue(semaphore2.tryAcquire());
        assertEquals(98, semaphore2.getRequestsAvailable());
    }

    @Test
    void Given_ApiLimit_UpdateItsValues() {

        ApiLimitRepository apiLimitRepository = new ApiLimitRepository();

        apiLimitRepository.createDefault(limitConfiguration);

        ApiLimitSemaphore semaphore = apiLimitRepository.getSemaphore(RequestLimit.POST).get();

        assertEquals(100, semaphore.getMaxPermits());
        assertEquals(100, semaphore.getRequestsAvailable());
        assertTrue(semaphore.tryAcquire());

        ApiLimit newLimit = new ApiLimit(200,Duration.ofMinutes(10));
        apiLimitRepository.updateLimitSemaphore(RequestLimit.POST, newLimit);

        semaphore = apiLimitRepository.getSemaphore(RequestLimit.POST).get();

        assertEquals(200, semaphore.getMaxPermits());
        assertEquals(200, semaphore.getRequestsAvailable());
        assertEquals(Duration.ofMinutes(10), semaphore.getDuration());

        for (int i = 0; i < 199; i++) {
            assertTrue(semaphore.tryAcquire());
        }
    }

    @Test
    public void Given_ApiLimit_When_Release_Then_ReuseNewConfiguration() throws InterruptedException {
        ApiLimitRepository apiLimitRepository = new ApiLimitRepository();

        apiLimitRepository.createDefault(limitConfiguration);

        ApiLimit newLimit = new ApiLimit(1,Duration.ofSeconds(1));

        apiLimitRepository.updateLimitSemaphore(RequestLimit.DELETE, newLimit);
        ApiLimitSemaphore semaphore = apiLimitRepository.getSemaphore(RequestLimit.DELETE).get();
        assertEquals(1, semaphore.getMaxPermits());
        assertEquals(1, semaphore.getRequestsAvailable());
        assertEquals(1, semaphore.getDuration().toSeconds());

        assertTrue(semaphore.tryAcquire());
        assertFalse(semaphore.tryAcquire());

        //Sleep to be possible release the semaphore
        Thread.sleep(2000);

        semaphore = apiLimitRepository.getSemaphore(RequestLimit.DELETE).get();
        assertEquals(1, semaphore.getMaxPermits());
        assertEquals(1, semaphore.getRequestsAvailable());
        assertEquals(1, semaphore.getDuration().toSeconds());

        assertTrue(semaphore.tryAcquire());
        assertFalse(semaphore.tryAcquire());
    }
}