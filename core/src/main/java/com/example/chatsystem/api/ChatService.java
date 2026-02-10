package com.example.chatsystem.api;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.ChatSystem;
import com.example.chatsystem.routing.ChatResult;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

/**
 * Public-facing API for interacting with the chat system.
 */
public interface ChatService {
    ChatResult send(String channelId, ChatMessage message);

    ChatResult sendAndRelay(String channelId, ChatMessage message);

    Optional<ChatChannel> findChannel(String channelId);

    Collection<ChatChannel> listChannels();

    /**
     * Returns the IDs of all registered channels.
     */
    Collection<String> listChannelIds();

    /**
     * Registers a new channel at runtime.
     */
    void registerChannel(ChatChannel channel);

    /**
     * Replaces an existing channel definition.
     */
    void updateChannel(ChatChannel channel);

    /**
     * Removes a channel by ID.
     */
    boolean removeChannel(String channelId);

    /**
     * Returns linked channel IDs for a channel.
     */
    Collection<String> listLinkedChannels(String channelId);

    /**
     * Returns recent message history for a channel.
     */
    Collection<com.example.chatsystem.storage.ChatHistory.ChatHistoryEntry> recentHistory(
            String channelId,
            int limit
    );

    /**
     * Checks whether a player can speak in a channel.
     */
    boolean canSpeak(UUID playerId, String channelId);

    /**
     * Wraps a {@link ChatSystem} instance in the API interface.
     */
    static ChatService from(ChatSystem chatSystem) {
        return new ChatServiceImpl(chatSystem);
    }
}
