package com.example.chatsystem.redis;

import java.util.function.Consumer;

/**
 * Minimal Redis subscriber abstraction for pluggable client libraries.
 */
public interface RedisSubscriber {
    void subscribe(String channel, Consumer<String> handler);
}
