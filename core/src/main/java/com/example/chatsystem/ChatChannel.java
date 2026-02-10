package com.example.chatsystem;

import java.util.List;
import java.util.Objects;

/**
 * Represents a configured chat channel and its permission/formatting defaults.
 */
public final class ChatChannel {
    private final String id;
    private final String displayName;
    private final String permission;
    private final String format;
    private final String formatPreset;
    private final boolean muted;
    private final RelayMode relayMode;
    private final List<String> linkedChannels;

    public ChatChannel(String id, String displayName, String permission, String format) {
        this(id, displayName, permission, format, null, false, RelayMode.LOCAL, List.of());
    }

    public ChatChannel(
            String id,
            String displayName,
            String permission,
            String format,
            String formatPreset,
            boolean muted,
            RelayMode relayMode,
            List<String> linkedChannels
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.displayName = Objects.requireNonNull(displayName, "displayName");
        this.permission = Objects.requireNonNull(permission, "permission");
        this.format = Objects.requireNonNull(format, "format");
        this.formatPreset = formatPreset;
        this.muted = muted;
        this.relayMode = Objects.requireNonNull(relayMode, "relayMode");
        this.linkedChannels = List.copyOf(Objects.requireNonNull(linkedChannels, "linkedChannels"));
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPermission() {
        return permission;
    }

    public String getFormat() {
        return format;
    }

    public String getFormatPreset() {
        return formatPreset;
    }

    public boolean isMuted() {
        return muted;
    }

    public RelayMode getRelayMode() {
        return relayMode;
    }

    public List<String> getLinkedChannels() {
        return linkedChannels;
    }

    public ChatChannel withFormat(String format) {
        return new ChatChannel(
                id,
                displayName,
                permission,
                format,
                formatPreset,
                muted,
                relayMode,
                linkedChannels
        );
    }

    public enum RelayMode {
        LOCAL,
        REDIS
    }
}
