package com.example.chatsystem.formatting;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.routing.ChatFormat;

/**
 * Formats messages for Adventure MiniMessage renderers.
 */
public final class MiniMessageFormatter implements MessageFormatter {
    @Override
    public String format(ChatChannel channel, ChatMessage message, String renderedContent) {
        return channel.getFormat()
                .replace("{channel}", channel.getDisplayName())
                .replace("{player}", message.getSenderName())
                .replace("{message}", renderedContent);
    }

    @Override
    public ChatFormat getFormat() {
        return ChatFormat.MINIMESSAGE;
    }
}
