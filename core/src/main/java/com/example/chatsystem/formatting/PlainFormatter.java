package com.example.chatsystem.formatting;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.routing.ChatFormat;

/**
 * Formats a message using a minimal [Channel] Player: Message pattern.
 */
public final class PlainFormatter implements MessageFormatter {
    @Override
    public String format(ChatChannel channel, ChatMessage message, String renderedContent) {
        return "[" + channel.getDisplayName() + "] " + message.getSenderName() + ": " + renderedContent;
    }

    @Override
    public ChatFormat getFormat() {
        return ChatFormat.PLAIN;
    }
}
