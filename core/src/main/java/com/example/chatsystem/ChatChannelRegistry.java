package com.example.chatsystem;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Stores the channel definitions used by the routing layer.
 */
public final class ChatChannelRegistry {
    private final Map<String, ChatChannel> channels = new LinkedHashMap<>();

    public void register(ChatChannel channel) {
        Objects.requireNonNull(channel, "channel");
        channels.put(channel.getId(), channel);
    }

    public Optional<ChatChannel> findById(String id) {
        return Optional.ofNullable(channels.get(id));
    }

    public Collection<ChatChannel> getAll() {
        return Collections.unmodifiableCollection(channels.values());
    }

    public boolean remove(String id) {
        return channels.remove(id) != null;
    }

    public void replace(ChatChannel channel) {
        Objects.requireNonNull(channel, "channel");
        if (!channels.containsKey(channel.getId())) {
            throw new IllegalArgumentException("Unknown channel: " + channel.getId());
        }
        channels.put(channel.getId(), channel);
    }
}
