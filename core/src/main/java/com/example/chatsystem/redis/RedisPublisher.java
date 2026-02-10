package com.example.chatsystem.redis;

/**
 * Minimal Redis publisher abstraction for pluggable client libraries.
 */
public interface RedisPublisher {
    void publish(String channel, String payload);
}
