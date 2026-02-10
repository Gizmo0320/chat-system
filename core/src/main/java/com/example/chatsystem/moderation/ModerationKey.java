package com.example.chatsystem.moderation;

import java.util.UUID;

/**
 * Key for moderation state storage.
 */
public record ModerationKey(UUID playerId, String channelId) {}
