package com.example.chatsystem.relay;

import com.example.chatsystem.ChatMessage;

/**
 * Rendered chat payload ready for relay to other servers.
 */
public record ChatRelayMessage(String channelId, ChatMessage message, String renderedMessage) {}
