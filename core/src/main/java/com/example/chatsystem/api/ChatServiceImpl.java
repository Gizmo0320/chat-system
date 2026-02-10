package com.example.chatsystem.api;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.ChatSystem;
import com.example.chatsystem.routing.ChatRouter;
import com.example.chatsystem.routing.ChatResult;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

final class ChatServiceImpl implements ChatService {
    private final ChatSystem chatSystem;

    ChatServiceImpl(ChatSystem chatSystem) {
        this.chatSystem = Objects.requireNonNull(chatSystem, "chatSystem");
    }

    @Override
    public ChatResult send(String channelId, ChatMessage message) {
        return chatSystem.send(channelId, message);
    }

    @Override
    public ChatResult sendAndRelay(String channelId, ChatMessage message) {
        return chatSystem.sendAndRelay(channelId, message);
    }

    @Override
    public Optional<ChatChannel> findChannel(String channelId) {
        return chatSystem.getChannelRegistry().findById(channelId);
    }

    @Override
    public Collection<ChatChannel> listChannels() {
        return chatSystem.getChannelRegistry().getAll();
    }

    @Override
    public Collection<String> listChannelIds() {
        return chatSystem.getChannelRegistry().getAll()
                .stream()
                .map(ChatChannel::getId)
                .toList();
    }

    @Override
    public void registerChannel(ChatChannel channel) {
        chatSystem.getChannelRegistry().register(channel);
    }

    @Override
    public void updateChannel(ChatChannel channel) {
        chatSystem.getChannelRegistry().replace(channel);
    }

    @Override
    public boolean removeChannel(String channelId) {
        return chatSystem.getChannelRegistry().remove(channelId);
    }

    @Override
    public Collection<String> listLinkedChannels(String channelId) {
        return findChannel(channelId)
                .map(ChatChannel::getLinkedChannels)
                .orElseGet(java.util.List::of);
    }

    @Override
    public Collection<com.example.chatsystem.storage.ChatHistory.ChatHistoryEntry> recentHistory(
            String channelId,
            int limit
    ) {
        return chatSystem.getRouter().getHistory().recent(channelId, limit);
    }

    @Override
    public boolean canSpeak(UUID playerId, String channelId) {
        ChatRouter router = chatSystem.getRouter();
        return router.canSpeak(playerId, channelId);
    }
}
