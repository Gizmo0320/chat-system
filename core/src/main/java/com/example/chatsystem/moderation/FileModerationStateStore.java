package com.example.chatsystem.moderation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

/**
 * Properties-backed moderation store for mutes and slow-mode timestamps.
 */
public final class FileModerationStateStore implements ModerationStateStore {
    private final Path path;

    public FileModerationStateStore(Path path) {
        this.path = Objects.requireNonNull(path, "path");
    }

    @Override
    public synchronized Set<SimpleModerationPolicy.ChannelMute> loadMutedPlayers() {
        Properties properties = loadProperties();
        Set<SimpleModerationPolicy.ChannelMute> muted = new HashSet<>();
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith("mute.")) {
                continue;
            }
            String[] parts = key.split("\\.", 3);
            if (parts.length < 3) {
                continue;
            }
            UUID playerId = UUID.fromString(parts[1]);
            String channelId = parts[2];
            muted.add(new SimpleModerationPolicy.ChannelMute(playerId, channelId));
        }
        return muted;
    }

    @Override
    public synchronized Map<ModerationKey, Instant> loadLastMessageAt() {
        Properties properties = loadProperties();
        Map<ModerationKey, Instant> lastMessages = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            if (!key.startsWith("last.")) {
                continue;
            }
            String[] parts = key.split("\\.", 3);
            if (parts.length < 3) {
                continue;
            }
            UUID playerId = UUID.fromString(parts[1]);
            String channelId = parts[2];
            String value = properties.getProperty(key);
            Instant timestamp = Instant.ofEpochMilli(Long.parseLong(value));
            lastMessages.put(new ModerationKey(playerId, channelId), timestamp);
        }
        return lastMessages;
    }

    @Override
    public synchronized void saveLastMessage(ModerationKey key, Instant timestamp) {
        Properties properties = loadProperties();
        properties.setProperty("last." + key.playerId() + "." + key.channelId(), String.valueOf(timestamp.toEpochMilli()));
        writeProperties(properties);
    }

    @Override
    public synchronized void saveMutedPlayers(Set<SimpleModerationPolicy.ChannelMute> mutedPlayers) {
        Properties properties = loadProperties();
        properties.entrySet().removeIf(entry -> entry.getKey().toString().startsWith("mute."));
        for (SimpleModerationPolicy.ChannelMute mute : mutedPlayers) {
            properties.setProperty("mute." + mute.playerId() + "." + mute.channelId(), "true");
        }
        writeProperties(properties);
    }

    private Properties loadProperties() {
        Properties properties = new Properties();
        if (!Files.exists(path)) {
            return properties;
        }
        try (InputStream input = Files.newInputStream(path, StandardOpenOption.READ)) {
            properties.load(input);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load moderation state", ex);
        }
        return properties;
    }

    private void writeProperties(Properties properties) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (OutputStream output = Files.newOutputStream(
                    path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )) {
                properties.store(output, "Chat moderation state");
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to persist moderation state", ex);
        }
    }
}
