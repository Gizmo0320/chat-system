package com.example.chatsystem;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable chat payload emitted by platform adapters and consumed by the routing pipeline.
 */
public final class ChatMessage {
    private final UUID senderId;
    private final String senderName;
    private final String content;
    private final Instant timestamp;
    private final String sourcePlatform;

    public ChatMessage(UUID senderId, String senderName, String content, Instant timestamp, String sourcePlatform) {
        this.senderId = Objects.requireNonNull(senderId, "senderId");
        this.senderName = Objects.requireNonNull(senderName, "senderName");
        this.content = Objects.requireNonNull(content, "content");
        this.timestamp = Objects.requireNonNull(timestamp, "timestamp");
        this.sourcePlatform = Objects.requireNonNull(sourcePlatform, "sourcePlatform");
    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getSourcePlatform() {
        return sourcePlatform;
    }
}
