package com.example.chatsystem.observability;

/**
 * No-op metrics sink.
 */
public final class NoopChatMetrics implements ChatMetrics {
    @Override
    public void incrementAllowed(String channelId) {
        // Intentionally empty.
    }

    @Override
    public void incrementDenied(String channelId, String reason) {
        // Intentionally empty.
    }
}
