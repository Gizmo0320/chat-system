package com.example.chatsystem.formatting;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatMessage;

import java.util.Objects;

/**
 * Formatter wrapper that resolves per-channel presets before formatting.
 */
public final class PresetAwareFormatter implements MessageFormatter {
    private final MessageFormatter delegate;
    private final FormatPresetRegistry presetRegistry;

    public PresetAwareFormatter(MessageFormatter delegate, FormatPresetRegistry presetRegistry) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.presetRegistry = Objects.requireNonNull(presetRegistry, "presetRegistry");
    }

    @Override
    public String format(ChatChannel channel, ChatMessage message, String renderedContent) {
        String resolvedTemplate = presetRegistry.resolveTemplate(channel);
        return delegate.format(channel.withFormat(resolvedTemplate), message, renderedContent);
    }

    @Override
    public com.example.chatsystem.routing.ChatFormat getFormat() {
        return delegate.getFormat();
    }
}
