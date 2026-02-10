package com.example.chatsystem.moderation;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * No-op moderation persistence implementation.
 */
public final class NoopModerationStateStore implements ModerationStateStore {
    @Override
    public Set<SimpleModerationPolicy.ChannelMute> loadMutedPlayers() {
        return Set.of();
    }

    @Override
    public Map<ModerationKey, Instant> loadLastMessageAt() {
        return Map.of();
    }

    @Override
    public void saveLastMessage(ModerationKey key, Instant timestamp) {
        // Intentionally empty.
    }

    @Override
    public void saveMutedPlayers(Set<SimpleModerationPolicy.ChannelMute> mutedPlayers) {
        // Intentionally empty.
    }
}
