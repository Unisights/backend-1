package com.unisights.backend.config;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SimpleRateLimiter {

    private final Map<String, RequestInfo> requestCounts = new ConcurrentHashMap<>();

    private static final int MAX_REQUESTS = 20;
    private static final long WINDOW_MS = 60_000;

    public boolean isAllowed(String clientId) {
        long now = Instant.now().toEpochMilli();

        requestCounts.compute(clientId, (key, info) -> {
            if (info == null || now - info.startTime > WINDOW_MS) {
                return new RequestInfo(1, now);
            } else if (info.count < MAX_REQUESTS) {
                info.count++;
                return info;
            } else {
                return info;
            }
        });

        return requestCounts.get(clientId).count <= MAX_REQUESTS;
    }

    private static class RequestInfo {
        int count;
        long startTime;

        RequestInfo(int count, long startTime) {
            this.count = count;
            this.startTime = startTime;
        }
    }
}
