package com.example.chatsystem.audit;

import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.routing.ChatDelivery;

import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Audit logger that writes routing decisions to {@link java.util.logging.Logger}.
 */
public final class LoggingChatAuditLogger implements ChatAuditLogger {
    private final Logger logger;

    public LoggingChatAuditLogger(Logger logger) {
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @Override
    public void onMessageAllowed(ChatMessage message, String channelId, List<ChatDelivery> deliveries) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(() -> String.format(
                    "Chat allowed: channel=%s sender=%s (%s) deliveries=%d",
                    channelId,
                    message.getSenderName(),
                    message.getSenderId(),
                    deliveries.size()
            ));
        }
    }

    @Override
    public void onMessageDenied(ChatMessage message, String channelId, String reason) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info(() -> String.format(
                    "Chat denied: channel=%s sender=%s (%s) reason=%s",
                    channelId,
                    message.getSenderName(),
                    message.getSenderId(),
                    reason
            ));
        }
    }
}
