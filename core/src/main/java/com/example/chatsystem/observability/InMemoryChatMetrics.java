package com.example.chatsystem.observability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 * In-memory metrics counters.
 */
public final class InMemoryChatMetrics implements ChatMetrics {
    private final Map<String, LongAdder> allowedByChannel = new ConcurrentHashMap<>();
    private final Map<String, LongAdder> deniedByReason = new ConcurrentHashMap<>();

    @Override
    public void incrementAllowed(String channelId) {
        allowedByChannel.computeIfAbsent(channelId, ignored -> new LongAdder()).increment();
    }

    @Override
    public void incrementDenied(String channelId, String reason) {
        deniedByReason.computeIfAbsent(channelId + "|" + reason, ignored -> new LongAdder()).increment();
    }

    public Map<String, LongAdder> getAllowedByChannel() {
        return allowedByChannel;
    }

    public Map<String, LongAdder> getDeniedByReason() {
        return deniedByReason;
    }
}
