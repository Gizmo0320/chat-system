package com.example.chatsystem.relay;

/**
 * Default relay implementation that performs no action.
 */
public final class NoopChatRelay implements ChatRelay {
    @Override
    public void relay(ChatRelayMessage message) {
        // Intentionally no-op.
    }
}
