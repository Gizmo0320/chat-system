package com.example.chatsystem.formatting;

import com.example.chatsystem.ChatChannel;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Registry for reusable formatting templates.
 */
public final class FormatPresetRegistry {
    private final Map<String, String> presets;

    public FormatPresetRegistry(Map<String, String> presets) {
        this.presets = Map.copyOf(Objects.requireNonNull(presets, "presets"));
    }

    public Optional<String> findPreset(String presetId) {
        if (presetId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(presets.get(presetId));
    }

    public String resolveTemplate(ChatChannel channel) {
        return findPreset(channel.getFormatPreset()).orElse(channel.getFormat());
    }
}
