package com.example.chatsystem.config;

import com.example.chatsystem.ChatChannel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal YAML reader for channel configuration.
 */
public final class SimpleYamlConfigLoader implements ChatConfigLoader {
    @Override
    public ChatConfig load(Path path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read config: " + path, ex);
        }

        List<ChatChannel> channels = new ArrayList<>();
        Map<String, String> formatPresets = new LinkedHashMap<>();
        boolean inChannels = false;
        boolean inLinkedChannels = false;
        boolean inFormatPresets = false;
        ChannelBuilder current = null;

        for (String rawLine : lines) {
            String line = rawLine.replace("\t", "    ");
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }

            if (trimmed.equals("channels:")) {
                inChannels = true;
                inFormatPresets = false;
                continue;
            }
            if (trimmed.equals("formatPresets:")) {
                if (current != null) {
                    channels.add(current.build());
                    current = null;
                }
                inChannels = false;
                inFormatPresets = true;
                continue;
            }

            if (inFormatPresets) {
                if (!line.startsWith("  ")) {
                    inFormatPresets = false;
                    continue;
                }
                String[] kv = trimmed.split(":", 2);
                if (kv.length == 2) {
                    formatPresets.put(kv[0].trim(), kv[1].trim().replace("\"", ""));
                }
                continue;
            }

            if (!inChannels) {
                continue;
            }

            if (!line.startsWith("  ")) {
                if (current != null) {
                    channels.add(current.build());
                    current = null;
                }
                inChannels = false;
                continue;
            }

            if (trimmed.startsWith("- id:")) {
                if (current != null) {
                    channels.add(current.build());
                }
                current = new ChannelBuilder();
                current.id = trimmed.substring(5).trim();
                inLinkedChannels = false;
                continue;
            }

            if (current == null) {
                continue;
            }

            if (trimmed.startsWith("linkedChannels:")) {
                inLinkedChannels = true;
                continue;
            }

            if (inLinkedChannels && trimmed.startsWith("- ")) {
                current.linkedChannels.add(trimmed.substring(2).trim());
                continue;
            }

            if (trimmed.startsWith("displayName:")) {
                current.displayName = trimmed.substring("displayName:".length()).trim();
            } else if (trimmed.startsWith("permission:")) {
                current.permission = trimmed.substring("permission:".length()).trim();
            } else if (trimmed.startsWith("format:")) {
                current.format = trimmed.substring("format:".length()).trim().replace("\"", "");
            } else if (trimmed.startsWith("formatPreset:")) {
                current.formatPreset = emptyToNull(trimmed.substring("formatPreset:".length()).trim());
            } else if (trimmed.startsWith("muted:")) {
                current.muted = Boolean.parseBoolean(trimmed.substring("muted:".length()).trim());
            } else if (trimmed.startsWith("relayMode:")) {
                current.relayMode = trimmed.substring("relayMode:".length()).trim();
            }
        }

        if (current != null) {
            channels.add(current.build());
        }

        return new ChatConfig(channels, formatPresets);
    }

    private static String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private static final class ChannelBuilder {
        private String id;
        private String displayName = "Channel";
        private String permission = "";
        private String format = "{player}: {message}";
        private String formatPreset;
        private boolean muted;
        private String relayMode = "LOCAL";
        private final List<String> linkedChannels = new ArrayList<>();

        private ChatChannel build() {
            return new ChatChannel(
                    id,
                    displayName,
                    permission,
                    format,
                    formatPreset,
                    muted,
                    ChatChannel.RelayMode.valueOf(relayMode),
                    linkedChannels
            );
        }
    }
}
