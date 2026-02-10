package com.example.chatsystem.formatting;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.routing.ChatFormat;

/**
 * Applies a legacy format template using the channel placeholders.
 */
public final class LegacyFormatter implements MessageFormatter {
    @Override
    public String format(ChatChannel channel, ChatMessage message, String renderedContent) {
        return channel.getFormat()
                .replace("{channel}", channel.getDisplayName())
                .replace("{player}", message.getSenderName())
                .replace("{message}", renderedContent);
    }

    @Override
    public ChatFormat getFormat() {
        return ChatFormat.LEGACY;
    }
}
