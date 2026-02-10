package com.example.chatsystem.config;

import com.example.chatsystem.ChatChannel;

import java.util.HashSet;
import java.util.Set;

/**
 * Validates loaded chat configuration.
 */
public final class ChatConfigValidator {
    public void validate(ChatConfig config) {
        Set<String> ids = new HashSet<>();
        Set<String> channelIds = new HashSet<>();
        for (ChatChannel channel : config.channels()) {
            if (channel.getId() == null || channel.getId().isBlank()) {
                throw new IllegalArgumentException("Channel id cannot be blank");
            }
            if (!ids.add(channel.getId())) {
                throw new IllegalArgumentException("Duplicate channel id: " + channel.getId());
            }
            if (channel.getPermission() == null || channel.getPermission().isBlank()) {
                throw new IllegalArgumentException("Channel permission cannot be blank: " + channel.getId());
            }
            channelIds.add(channel.getId());
            if (channel.getFormatPreset() != null && !config.formatPresets().containsKey(channel.getFormatPreset())) {
                throw new IllegalArgumentException("Unknown format preset for channel " + channel.getId() + ": " + channel.getFormatPreset());
            }
        }

        for (ChatChannel channel : config.channels()) {
            for (String linked : channel.getLinkedChannels()) {
                if (!channelIds.contains(linked)) {
                    throw new IllegalArgumentException(
                            "Channel " + channel.getId() + " links to unknown channel " + linked
                    );
                }
            }
        }
    }
}
