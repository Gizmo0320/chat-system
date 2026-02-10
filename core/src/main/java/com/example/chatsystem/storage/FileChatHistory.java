package com.example.chatsystem.storage;

import com.example.chatsystem.ChatMessage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * File-backed history store with configurable retention and optional payload cipher.
 */
public final class FileChatHistory implements ChatHistory {
    private final Path path;
    private final int maxEntries;
    private final HistoryCipher cipher;
    private final ContentRedactor redactor;

    public FileChatHistory(Path path) {
        this(path, 5_000, HistoryCipher.none(), ContentRedactor.none());
    }

    public FileChatHistory(Path path, int maxEntries, HistoryCipher cipher, ContentRedactor redactor) {
        this.path = Objects.requireNonNull(path, "path");
        this.maxEntries = Math.max(1, maxEntries);
        this.cipher = Objects.requireNonNull(cipher, "cipher");
        this.redactor = Objects.requireNonNull(redactor, "redactor");
    }

    @Override
    public synchronized void append(String channelId, ChatMessage message, String renderedMessage) {
        String record = encodeRecord(channelId, message, renderedMessage);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(
                    path,
                    record + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
            );
            enforceRetention();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to append chat history", ex);
        }
    }

    @Override
    public synchronized List<ChatHistoryEntry> recent(String channelId, int limit) {
        if (!Files.exists(path)) {
            return List.of();
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read chat history", ex);
        }
        List<ChatHistoryEntry> entries = new ArrayList<>();
        for (int index = lines.size() - 1; index >= 0 && entries.size() < limit; index--) {
            String line = lines.get(index).trim();
            if (line.isEmpty()) {
                continue;
            }
            ParsedRecord parsed = decodeRecord(line);
            if (parsed != null && parsed.channelId().equals(channelId)) {
                entries.add(new ChatHistoryEntry(parsed.message(), parsed.rendered()));
            }
        }
        return entries;
    }

    private void enforceRetention() throws IOException {
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (lines.size() <= maxEntries) {
            return;
        }
        int from = lines.size() - maxEntries;
        List<String> trimmed = new ArrayList<>(lines.subList(from, lines.size()));
        Files.write(path, trimmed, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    private String encodeRecord(String channelId, ChatMessage message, String renderedMessage) {
        String safeContent = redactor.redact(message.getContent());
        String safeRendered = redactor.redact(renderedMessage);
        String data = String.join(
                "|",
                channelId,
                message.getSenderId().toString(),
                message.getSenderName(),
                safeContent,
                message.getSourcePlatform(),
                String.valueOf(message.getTimestamp().toEpochMilli()),
                safeRendered
        );
        byte[] encrypted = cipher.encrypt(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private ParsedRecord decodeRecord(String encoded) {
        try {
            byte[] encrypted = Base64.getDecoder().decode(encoded);
            String decoded = new String(cipher.decrypt(encrypted), StandardCharsets.UTF_8);
            String[] parts = decoded.split("\\|", -1);
            if (parts.length < 7) {
                return null;
            }
            String channelId = parts[0];
            UUID senderId = UUID.fromString(parts[1]);
            String senderName = parts[2];
            String content = parts[3];
            String sourcePlatform = parts[4];
            Instant timestamp = Instant.ofEpochMilli(Long.parseLong(parts[5]));
            String rendered = parts[6];
            ChatMessage message = new ChatMessage(senderId, senderName, content, timestamp, sourcePlatform);
            return new ParsedRecord(channelId, message, rendered);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private record ParsedRecord(String channelId, ChatMessage message, String rendered) {}

    public interface ContentRedactor {
        String redact(String value);

        static ContentRedactor none() {
            return value -> value;
        }
    }

    public interface HistoryCipher {
        byte[] encrypt(byte[] value);

        byte[] decrypt(byte[] value);

        static HistoryCipher none() {
            return new HistoryCipher() {
                @Override
                public byte[] encrypt(byte[] value) { return value; }

                @Override
                public byte[] decrypt(byte[] value) { return value; }
            };
        }

        static HistoryCipher xor(String secret) {
            if (secret == null || secret.isEmpty()) {
                return none();
            }
            final byte[] key = secret.getBytes(StandardCharsets.UTF_8);
            return new HistoryCipher() {
                @Override
                public byte[] encrypt(byte[] value) {
                    byte[] out = new byte[value.length];
                    for (int i = 0; i < value.length; i++) {
                        out[i] = (byte) (value[i] ^ key[i % key.length]);
                    }
                    return out;
                }

                @Override
                public byte[] decrypt(byte[] value) {
                    return encrypt(value);
                }
            };
        }
    }
}
