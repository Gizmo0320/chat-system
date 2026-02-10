package com.example.chatsystem.observability;

/**
 * Metrics sink for routing events.
 */
public interface ChatMetrics {
    void incrementAllowed(String channelId);

    void incrementDenied(String channelId, String reason);
}
