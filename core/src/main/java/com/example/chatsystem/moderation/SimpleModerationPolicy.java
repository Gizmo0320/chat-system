package com.example.chatsystem.moderation;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enforces per-channel slow mode and mute lists with in-memory tracking.
 */
public final class SimpleModerationPolicy implements ChatModerationPolicy {
    private final Map<String, Integer> slowModeSecondsByChannel;
    private final Set<ChannelMute> mutedPlayers;
    private final Map<ModerationKey, Instant> lastMessageAt = new ConcurrentHashMap<>();
    private final ModerationStateStore stateStore;

    public SimpleModerationPolicy(Map<String, Integer> slowModeSecondsByChannel, Set<ChannelMute> mutedPlayers) {
        this(slowModeSecondsByChannel, mutedPlayers, new NoopModerationStateStore());
    }

    public SimpleModerationPolicy(
            Map<String, Integer> slowModeSecondsByChannel,
            Set<ChannelMute> mutedPlayers,
            ModerationStateStore stateStore
    ) {
        this.slowModeSecondsByChannel = Map.copyOf(slowModeSecondsByChannel);
        this.stateStore = stateStore == null ? new NoopModerationStateStore() : stateStore;
        Set<ChannelMute> mergedMutes = new HashSet<>(mutedPlayers);
        mergedMutes.addAll(this.stateStore.loadMutedPlayers());
        this.mutedPlayers = Set.copyOf(mergedMutes);
        this.lastMessageAt.putAll(this.stateStore.loadLastMessageAt());
        this.stateStore.saveMutedPlayers(this.mutedPlayers);
    }

    @Override
    public ModerationResult evaluate(UUID playerId, String channelId, Instant timestamp) {
        ChannelMute mute = new ChannelMute(playerId, channelId);
        if (mutedPlayers.contains(mute)) {
            return ModerationResult.deny("You are muted in this channel.");
        }

        int slowModeSeconds = slowModeSecondsByChannel.getOrDefault(channelId, 0);
        if (slowModeSeconds <= 0) {
            return ModerationResult.allow();
        }

        ModerationKey key = new ModerationKey(playerId, channelId);
        Instant lastMessage = lastMessageAt.get(key);
        if (lastMessage == null) {
            return ModerationResult.allow();
        }

        Duration elapsed = Duration.between(lastMessage, timestamp);
        if (elapsed.getSeconds() < slowModeSeconds) {
            long remaining = slowModeSeconds - elapsed.getSeconds();
            return ModerationResult.deny("Slow mode is enabled. Wait " + remaining + "s.");
        }

        return ModerationResult.allow();
    }

    @Override
    public void recordMessage(UUID playerId, String channelId, Instant timestamp) {
        ModerationKey key = new ModerationKey(playerId, channelId);
        lastMessageAt.put(key, timestamp);
        stateStore.saveLastMessage(key, timestamp);
    }

    public record ChannelMute(UUID playerId, String channelId) {}
}
