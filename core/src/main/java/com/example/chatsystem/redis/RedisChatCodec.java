package com.example.chatsystem.redis;

import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.relay.ChatRelayMessage;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Encodes and decodes relay messages for transport over Redis pub/sub.
 */
public final class RedisChatCodec {
    private static final String DELIMITER = "|";

    private RedisChatCodec() {
    }

    public static String encode(ChatRelayMessage relayMessage) {
        ChatMessage message = relayMessage.message();
        return String.join(
                DELIMITER,
                encodeString(relayMessage.channelId()),
                message.getSenderId().toString(),
                encodeString(message.getSenderName()),
                encodeString(message.getContent()),
                Long.toString(message.getTimestamp().toEpochMilli()),
                encodeString(message.getSourcePlatform()),
                encodeString(relayMessage.renderedMessage())
        );
    }

    public static ChatRelayMessage decode(String payload) {
        String[] parts = payload.split("\\|", -1);
        if (parts.length != 7) {
            throw new IllegalArgumentException("Invalid relay payload");
        }
        String channelId = decodeString(parts[0]);
        UUID senderId = UUID.fromString(parts[1]);
        String senderName = decodeString(parts[2]);
        String content = decodeString(parts[3]);
        Instant timestamp = Instant.ofEpochMilli(Long.parseLong(parts[4]));
        String sourcePlatform = decodeString(parts[5]);
        String renderedMessage = decodeString(parts[6]);
        ChatMessage message = new ChatMessage(senderId, senderName, content, timestamp, sourcePlatform);
        return new ChatRelayMessage(channelId, message, renderedMessage);
    }

    private static String encodeString(String value) {
        return Base64.getUrlEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decodeString(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
