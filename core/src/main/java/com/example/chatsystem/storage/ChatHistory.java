package com.example.chatsystem.storage;

import com.example.chatsystem.ChatMessage;

import java.util.List;

/**
 * Storage abstraction for retaining recent chat messages.
 */
public interface ChatHistory {
    void append(String channelId, ChatMessage message, String renderedMessage);

    List<ChatHistoryEntry> recent(String channelId, int limit);

    /**
     * Stored history entry.
     */
    record ChatHistoryEntry(ChatMessage message, String renderedMessage) {}
}
