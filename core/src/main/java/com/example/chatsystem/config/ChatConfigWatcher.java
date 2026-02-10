package com.example.chatsystem.config;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Watches a config file and reloads when changes are detected.
 */
public final class ChatConfigWatcher implements AutoCloseable {
    private final Path configPath;
    private final ChatConfigLoader loader;
    private final Consumer<ChatConfig> onReload;
    private WatchService watchService;
    private Thread thread;

    public ChatConfigWatcher(Path configPath, ChatConfigLoader loader, Consumer<ChatConfig> onReload) {
        this.configPath = Objects.requireNonNull(configPath, "configPath");
        this.loader = Objects.requireNonNull(loader, "loader");
        this.onReload = Objects.requireNonNull(onReload, "onReload");
    }

    public void start() throws IOException {
        if (watchService != null) {
            return;
        }
        watchService = FileSystems.getDefault().newWatchService();
        configPath.getParent().register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY
        );
        thread = new Thread(this::runLoop, "chat-config-watcher");
        thread.setDaemon(true);
        thread.start();
    }

    private void runLoop() {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                Path changed = (Path) event.context();
                if (changed != null && changed.endsWith(configPath.getFileName())) {
                    ChatConfig config = loader.load(configPath);
                    onReload.accept(config);
                }
            }
            if (!key.reset()) {
                return;
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (watchService != null) {
            watchService.close();
        }
        if (thread != null) {
            thread.interrupt();
        }
    }
}
