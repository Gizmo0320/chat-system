package com.example.chatsystem.moderation;

import java.time.Instant;
import java.util.UUID;

/**
 * Permissive moderation policy that never blocks messages.
 */
public final class AllowAllModerationPolicy implements ChatModerationPolicy {
    @Override
    public ModerationResult evaluate(UUID playerId, String channelId, Instant timestamp) {
        return ModerationResult.allow();
    }
}
