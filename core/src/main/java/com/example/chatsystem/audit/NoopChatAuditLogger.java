package com.example.chatsystem.audit;

import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.routing.ChatDelivery;

import java.util.List;

/**
 * No-op audit logger used by default.
 */
public final class NoopChatAuditLogger implements ChatAuditLogger {
    @Override
    public void onMessageAllowed(ChatMessage message, String channelId, List<ChatDelivery> deliveries) {
        // Intentionally empty.
    }

    @Override
    public void onMessageDenied(ChatMessage message, String channelId, String reason) {
        // Intentionally empty.
    }
}
