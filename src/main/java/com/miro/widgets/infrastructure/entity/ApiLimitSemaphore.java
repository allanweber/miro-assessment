package com.miro.widgets.infrastructure.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Getter
public final class ApiLimitSemaphore {

    private final Semaphore semaphore;
    private final Integer maxPermits;
    private final Duration duration;
    private final LocalDateTime startTime;

    public static ApiLimitSemaphore create(Integer permits, Duration timePeriod) {
        ApiLimitSemaphore limiter = new ApiLimitSemaphore(permits, timePeriod);
        limiter.setExecutor();
        return limiter;
    }

    private ApiLimitSemaphore(int permits, Duration duration) {
        this.semaphore = new Semaphore(permits);
        this.maxPermits = permits;
        this.duration = duration;
        this.startTime = LocalDateTime.now();
    }

    public LocalDateTime getNextReset() {
        return startTime.plusSeconds(duration.toSeconds());
    }

    public Integer getRequestsAvailable() {
        return semaphore.availablePermits();
    }

    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }

    public void release() {
        semaphore.release();
    }

    public void setExecutor() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            semaphore.release(maxPermits - semaphore.availablePermits());
        }, duration.toSeconds(), TimeUnit.SECONDS);
    }
}
