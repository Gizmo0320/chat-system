package com.example.chatsystem.relay;

/**
 * Relays rendered chat messages to external systems (e.g., cross-server transports).
 */
public interface ChatRelay {
    void relay(ChatRelayMessage message);
}
