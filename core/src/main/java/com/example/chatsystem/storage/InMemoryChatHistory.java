package com.example.chatsystem.storage;

import com.example.chatsystem.ChatMessage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory history store with bounded per-channel entries.
 */
public final class InMemoryChatHistory implements ChatHistory {
    private final Map<String, Deque<ChatHistoryEntry>> entries = new ConcurrentHashMap<>();
    private final int maxEntries;

    public InMemoryChatHistory(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    @Override
    public void append(String channelId, ChatMessage message, String renderedMessage) {
        Deque<ChatHistoryEntry> channelEntries = entries.computeIfAbsent(channelId, key -> new ArrayDeque<>());
        synchronized (channelEntries) {
            channelEntries.addLast(new ChatHistoryEntry(message, renderedMessage));
            while (channelEntries.size() > maxEntries) {
                channelEntries.removeFirst();
            }
        }
    }

    @Override
    public List<ChatHistoryEntry> recent(String channelId, int limit) {
        Deque<ChatHistoryEntry> channelEntries = entries.getOrDefault(channelId, new ArrayDeque<>());
        List<ChatHistoryEntry> result = new ArrayList<>();
        synchronized (channelEntries) {
            int count = 0;
            for (ChatHistoryEntry entry : channelEntries) {
                if (count++ >= limit) {
                    break;
                }
                result.add(entry);
            }
        }
        return result;
    }
}
