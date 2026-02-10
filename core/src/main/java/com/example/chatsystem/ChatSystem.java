package com.example.chatsystem;

import com.example.chatsystem.filters.ChatFilter;
import com.example.chatsystem.filters.ChatFilterPipeline;
import com.example.chatsystem.formatting.MessageFormatter;
import com.example.chatsystem.audit.ChatAuditLogger;
import com.example.chatsystem.audit.NoopChatAuditLogger;
import com.example.chatsystem.moderation.ChatModerationPolicy;
import com.example.chatsystem.permissions.PermissionChecker;
import com.example.chatsystem.relay.ChatRelay;
import com.example.chatsystem.relay.ChatRelayMessage;
import com.example.chatsystem.relay.NoopChatRelay;
import com.example.chatsystem.routing.ChatResult;
import com.example.chatsystem.routing.ChatRouter;
import com.example.chatsystem.storage.ChatHistory;
import com.example.chatsystem.observability.ChatMetrics;
import com.example.chatsystem.observability.NoopChatMetrics;

import java.util.List;
import java.util.Objects;

/**
 * Facade for routing chat messages through filters, formatting, moderation, and permissions.
 */
public final class ChatSystem {
    private final ChatRouter router;
    private final ChatChannelRegistry channelRegistry;
    private final ChatRelay relay;

    private ChatSystem(ChatRouter router, ChatChannelRegistry channelRegistry, ChatRelay relay) {
        this.router = router;
        this.channelRegistry = channelRegistry;
        this.relay = relay;
    }

    public ChatResult send(String channelId, ChatMessage message) {
        return router.route(channelId, message);
    }

    /**
     * Routes the message and relays the rendered output when permitted.
     */
    public ChatResult sendAndRelay(String channelId, ChatMessage message) {
        ChatResult result = send(channelId, message);
        if (result.allowed()) {
            for (var delivery : result.deliveries()) {
                channelRegistry.findById(delivery.channelId()).ifPresent(channel -> {
                    if (channel.getRelayMode() == ChatChannel.RelayMode.REDIS) {
                        relay.relay(new ChatRelayMessage(delivery.channelId(), message, delivery.renderedMessage()));
                    }
                });
            }
        }
        return result;
    }

    public ChatChannelRegistry getChannelRegistry() {
        return channelRegistry;
    }

    public ChatRouter getRouter() {
        return router;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for assembling a chat system with platform-specific adapters.
     */
    public static final class Builder {
        private final ChatChannelRegistry channelRegistry = new ChatChannelRegistry();
        private List<ChatFilter> filters = List.of();
        private ChatFilterPipeline filterPipeline;
        private MessageFormatter formatter;
        private ChatModerationPolicy moderationPolicy;
        private PermissionChecker permissionChecker;
        private ChatHistory history;
        private ChatRelay relay;
        private ChatAuditLogger auditLogger;
        private ChatMetrics metrics;

        public Builder channels(List<ChatChannel> channels) {
            Objects.requireNonNull(channels, "channels");
            channels.forEach(channelRegistry::register);
            return this;
        }

        public Builder addChannel(ChatChannel channel) {
            channelRegistry.register(Objects.requireNonNull(channel, "channel"));
            return this;
        }

        public Builder addChannels(List<ChatChannel> channels) {
            return channels(channels);
        }

        public Builder filters(List<ChatFilter> filters) {
            this.filters = List.copyOf(Objects.requireNonNull(filters, "filters"));
            return this;
        }

        public Builder filterPipeline(ChatFilterPipeline filterPipeline) {
            this.filterPipeline = Objects.requireNonNull(filterPipeline, "filterPipeline");
            return this;
        }

        public Builder formatter(MessageFormatter formatter) {
            this.formatter = Objects.requireNonNull(formatter, "formatter");
            return this;
        }

        public Builder moderationPolicy(ChatModerationPolicy moderationPolicy) {
            this.moderationPolicy = Objects.requireNonNull(moderationPolicy, "moderationPolicy");
            return this;
        }

        public Builder permissionChecker(PermissionChecker permissionChecker) {
            this.permissionChecker = Objects.requireNonNull(permissionChecker, "permissionChecker");
            return this;
        }

        public Builder history(ChatHistory history) {
            this.history = Objects.requireNonNull(history, "history");
            return this;
        }

        public Builder relay(ChatRelay relay) {
            this.relay = Objects.requireNonNull(relay, "relay");
            return this;
        }

        public Builder metrics(ChatMetrics metrics) {
            this.metrics = Objects.requireNonNull(metrics, "metrics");
            return this;
        }

        public Builder auditLogger(ChatAuditLogger auditLogger) {
            this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
            return this;
        }

        public ChatSystem build() {
            Objects.requireNonNull(formatter, "formatter");
            Objects.requireNonNull(moderationPolicy, "moderationPolicy");
            Objects.requireNonNull(permissionChecker, "permissionChecker");
            Objects.requireNonNull(history, "history");
            if (relay == null) {
                relay = new NoopChatRelay();
            }
            if (auditLogger == null) {
                auditLogger = new NoopChatAuditLogger();
            }
            if (metrics == null) {
                metrics = new NoopChatMetrics();
            }
            if (filterPipeline == null) {
                filterPipeline = ChatFilterPipeline.builder().addAll(filters).build();
            }

            ChatRouter router = new ChatRouter(
                    channelRegistry,
                    filterPipeline,
                    formatter,
                    moderationPolicy,
                    permissionChecker,
                    history,
                    auditLogger,
                    metrics
            );
            return new ChatSystem(router, channelRegistry, relay);
        }
    }
}
