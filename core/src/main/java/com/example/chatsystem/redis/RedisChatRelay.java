package com.example.chatsystem.redis;

import com.example.chatsystem.relay.ChatRelay;
import com.example.chatsystem.relay.ChatRelayMessage;

/**
 * Relays rendered chat messages to Redis using pub/sub.
 */
public final class RedisChatRelay implements ChatRelay {
    private final RedisPublisher publisher;
    private final String channel;

    public RedisChatRelay(RedisPublisher publisher, String channel) {
        this.publisher = publisher;
        this.channel = channel;
    }

    @Override
    public void relay(ChatRelayMessage message) {
        String payload = RedisChatCodec.encode(message);
        publisher.publish(channel, payload);
    }
}
