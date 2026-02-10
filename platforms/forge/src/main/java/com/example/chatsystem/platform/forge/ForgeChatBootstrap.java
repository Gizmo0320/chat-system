package com.example.chatsystem.platform.forge;

import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.admin.AdminCommandHandler;
import com.example.chatsystem.ChatSystem;
import com.example.chatsystem.routing.ChatResult;

import java.time.Instant;
import java.util.UUID;

/**
 * Forge adapter entry point that wires platform chat events into the shared chat system.
 */
public final class ForgeChatBootstrap {
    private final ChatSystem chatSystem;
    private final PlatformChatHandler chatHandler;
    private final AdminCommandHandler adminCommandHandler;

    public ForgeChatBootstrap(ChatSystem chatSystem, PlatformChatHandler chatHandler) {
        this.chatSystem = chatSystem;
        this.chatHandler = chatHandler;
        this.adminCommandHandler = new AdminCommandHandler(chatSystem);
    }

    public void onChat(String channelId, UUID senderId, String senderName, String content) {
        ChatMessage message = new ChatMessage(senderId, senderName, content, Instant.now(), "forge");
        ChatResult result = chatSystem.sendAndRelay(channelId, message);

        if (!result.allowed()) {
            chatHandler.notifyDenied(senderId, result.denialReason());
            return;
        }

        for (var delivery : result.deliveries()) {
            chatHandler.broadcast(delivery.renderedMessage(), delivery.format());
        }
    }


    public void onAdminCommand(UUID senderId, String commandLine) {
        String result = adminCommandHandler.execute(commandLine);
        chatHandler.notifyAdmin(senderId, result);
    }

    /**
     * Callback surface for platform-specific messaging APIs.
     */
    public interface PlatformChatHandler {
        void notifyDenied(UUID senderId, String reason);

        void broadcast(String renderedMessage, com.example.chatsystem.routing.ChatFormat format);

        void notifyAdmin(UUID senderId, String message);
    }
}
