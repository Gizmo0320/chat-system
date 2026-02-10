package com.example.chatsystem.formatting;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.routing.ChatFormat;

/**
 * Formats a channel message into a rendered string for platform adapters.
 */
public interface MessageFormatter {
    String format(ChatChannel channel, ChatMessage message, String renderedContent);

    /**
     * Declares the output format for the rendered message.
     */
    default ChatFormat getFormat() {
        return ChatFormat.LEGACY;
    }
}
