package com.example.chatsystem.config;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Loader wrapper that validates and preserves the last known-good config on reload failure.
 */
public final class SafeChatConfigLoader implements ChatConfigLoader {
    private final ChatConfigLoader delegate;
    private final ChatConfigValidator validator;
    private volatile ChatConfig lastKnownGood;

    public SafeChatConfigLoader(ChatConfigLoader delegate, ChatConfigValidator validator) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
        this.validator = Objects.requireNonNull(validator, "validator");
    }

    @Override
    public synchronized ChatConfig load(Path path) {
        try {
            ChatConfig config = delegate.load(path);
            validator.validate(config);
            lastKnownGood = config;
            return config;
        } catch (RuntimeException ex) {
            if (lastKnownGood != null) {
                return lastKnownGood;
            }
            throw ex;
        }
    }
}
