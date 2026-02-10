package com.example.chatsystem.moderation;

import java.time.Instant;
import java.util.UUID;

/**
 * Moderation hook for muting, slow mode, and other channel restrictions.
 */
public interface ChatModerationPolicy {
    ModerationResult evaluate(UUID playerId, String channelId, Instant timestamp);

    default void recordMessage(UUID playerId, String channelId, Instant timestamp) {
        // Default no-op.
    }

    /**
     * Outcome of a moderation evaluation.
     */
    record ModerationResult(boolean allowed, String reason) {
        public static ModerationResult allow() {
            return new ModerationResult(true, null);
        }

        public static ModerationResult deny(String reason) {
            return new ModerationResult(false, reason);
        }
    }
}
