package com.example.chatsystem.redis;

import com.example.chatsystem.relay.ChatRelayMessage;

/**
 * Subscribes to Redis relay messages and forwards them to a handler.
 */
public final class RedisChatSubscriber {
    private final RedisSubscriber subscriber;
    private final String channel;
    private final RedisRelayHandler handler;

    public RedisChatSubscriber(RedisSubscriber subscriber, String channel, RedisRelayHandler handler) {
        this.subscriber = subscriber;
        this.channel = channel;
        this.handler = handler;
    }

    public void start() {
        subscriber.subscribe(channel, this::handlePayload);
    }

    private void handlePayload(String payload) {
        ChatRelayMessage message = RedisChatCodec.decode(payload);
        handler.onRelay(message);
    }

    /**
     * Callback invoked when a relay message is received.
     */
    public interface RedisRelayHandler {
        void onRelay(ChatRelayMessage message);
    }
}
