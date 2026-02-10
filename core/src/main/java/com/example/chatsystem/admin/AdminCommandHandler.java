package com.example.chatsystem.admin;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatSystem;

import java.util.Optional;

/**
 * Handles admin chat commands.
 */
public final class AdminCommandHandler {
    private final ChatSystem chatSystem;

    public AdminCommandHandler(ChatSystem chatSystem) {
        this.chatSystem = chatSystem;
    }

    public String execute(String commandLine) {
        String[] parts = commandLine.trim().split("\\s+");
        if (parts.length < 3 || !parts[0].equalsIgnoreCase("channel") || !parts[1].equalsIgnoreCase("mute")) {
            return "Usage: channel mute <channelId> <true|false>";
        }
        if (parts.length < 4) {
            return "Usage: channel mute <channelId> <true|false>";
        }

        String channelId = parts[2];
        boolean muted = Boolean.parseBoolean(parts[3]);
        Optional<ChatChannel> existing = chatSystem.getChannelRegistry().findById(channelId);
        if (existing.isEmpty()) {
            return "Unknown channel: " + channelId;
        }
        ChatChannel channel = existing.get();
        ChatChannel updated = new ChatChannel(
                channel.getId(),
                channel.getDisplayName(),
                channel.getPermission(),
                channel.getFormat(),
                channel.getFormatPreset(),
                muted,
                channel.getRelayMode(),
                channel.getLinkedChannels()
        );
        chatSystem.getChannelRegistry().replace(updated);
        return "Channel " + channelId + " muted=" + muted;
    }
}
