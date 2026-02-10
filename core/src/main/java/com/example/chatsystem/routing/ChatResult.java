package com.example.chatsystem.routing;

import java.util.List;

/**
 * Routing outcome containing the rendered message or denial information.
 */
public record ChatResult(
        boolean allowed,
        String denialReason,
        String renderedMessage,
        List<ChatDelivery> deliveries
) {
    public static ChatResult denied(String reason) {
        return new ChatResult(false, reason, null, List.of());
    }

    public static ChatResult allowed(String renderedMessage, List<ChatDelivery> deliveries) {
        return new ChatResult(true, null, renderedMessage, deliveries);
    }
}
