package com.example.chatsystem.audit;

import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.routing.ChatDelivery;

import java.util.List;

/**
 * Hook for auditing chat routing decisions.
 */
public interface ChatAuditLogger {
    void onMessageAllowed(ChatMessage message, String channelId, List<ChatDelivery> deliveries);

    void onMessageDenied(ChatMessage message, String channelId, String reason);
}
