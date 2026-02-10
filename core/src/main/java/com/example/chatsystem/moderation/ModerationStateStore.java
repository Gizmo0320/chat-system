package com.example.chatsystem.moderation;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Persistence abstraction for moderation state like mutes and slow-mode tracking.
 */
public interface ModerationStateStore {
    Set<SimpleModerationPolicy.ChannelMute> loadMutedPlayers();

    Map<ModerationKey, Instant> loadLastMessageAt();

    void saveLastMessage(ModerationKey key, Instant timestamp);

    void saveMutedPlayers(Set<SimpleModerationPolicy.ChannelMute> mutedPlayers);
}
