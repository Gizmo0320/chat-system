package com.example.chatsystem.config;

import java.nio.file.Path;

/**
 * Loads chat configuration from disk.
 */
public interface ChatConfigLoader {
    ChatConfig load(Path path);
}
