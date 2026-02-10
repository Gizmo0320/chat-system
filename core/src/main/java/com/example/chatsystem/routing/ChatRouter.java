package com.example.chatsystem.routing;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatChannelRegistry;
import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.audit.ChatAuditLogger;
import com.example.chatsystem.filters.ChatFilterPipeline;
import com.example.chatsystem.formatting.MessageFormatter;
import com.example.chatsystem.moderation.ChatModerationPolicy;
import com.example.chatsystem.permissions.PermissionChecker;
import com.example.chatsystem.storage.ChatHistory;
import com.example.chatsystem.observability.ChatMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Applies permissions, moderation, filters, and formatting to incoming messages.
 */
public final class ChatRouter {
    private final ChatChannelRegistry channelRegistry;
    private final ChatFilterPipeline filterPipeline;
    private final MessageFormatter formatter;
    private final ChatModerationPolicy moderationPolicy;
    private final PermissionChecker permissionChecker;
    private final ChatHistory history;
    private final ChatAuditLogger auditLogger;
    private final ChatMetrics metrics;

    public ChatRouter(
            ChatChannelRegistry channelRegistry,
            ChatFilterPipeline filterPipeline,
            MessageFormatter formatter,
            ChatModerationPolicy moderationPolicy,
            PermissionChecker permissionChecker,
            ChatHistory history,
            ChatAuditLogger auditLogger,
            ChatMetrics metrics
    ) {
        this.channelRegistry = Objects.requireNonNull(channelRegistry, "channelRegistry");
        this.filterPipeline = Objects.requireNonNull(filterPipeline, "filterPipeline");
        this.formatter = Objects.requireNonNull(formatter, "formatter");
        this.moderationPolicy = Objects.requireNonNull(moderationPolicy, "moderationPolicy");
        this.permissionChecker = Objects.requireNonNull(permissionChecker, "permissionChecker");
        this.history = Objects.requireNonNull(history, "history");
        this.auditLogger = Objects.requireNonNull(auditLogger, "auditLogger");
        this.metrics = Objects.requireNonNull(metrics, "metrics");
    }

    public ChatResult route(String channelId, ChatMessage message) {
        ChatChannel channel = channelRegistry.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown channel: " + channelId));

        if (channel.isMuted()) {
            String reason = channel.getDisplayName() + " is currently muted.";
            auditLogger.onMessageDenied(message, channelId, reason);
            metrics.incrementDenied(channelId, reason);
            return ChatResult.denied(reason);
        }

        if (!permissionChecker.hasPermission(message.getSenderId(), channel.getPermission())) {
            String reason = "You lack permission for " + channel.getDisplayName();
            auditLogger.onMessageDenied(message, channelId, reason);
            metrics.incrementDenied(channelId, reason);
            return ChatResult.denied(reason);
        }

        ChatModerationPolicy.ModerationResult moderationResult = moderationPolicy.evaluate(
                message.getSenderId(),
                channelId,
                message.getTimestamp()
        );
        if (!moderationResult.allowed()) {
            auditLogger.onMessageDenied(message, channelId, moderationResult.reason());
            metrics.incrementDenied(channelId, moderationResult.reason());
            return ChatResult.denied(moderationResult.reason());
        }

        ChatFilterPipeline.FilterResult filterResult = filterPipeline.apply(message);
        if (!filterResult.allowed()) {
            auditLogger.onMessageDenied(message, channelId, filterResult.reason());
            metrics.incrementDenied(channelId, filterResult.reason());
            return ChatResult.denied(filterResult.reason());
        }
        String content = filterResult.content();

        String rendered = formatter.format(channel, message, content);
        history.append(channelId, message, rendered);
        moderationPolicy.recordMessage(message.getSenderId(), channelId, message.getTimestamp());
        List<ChatDelivery> deliveries = new ArrayList<>();
        ChatFormat format = formatter.getFormat();
        deliveries.add(new ChatDelivery(channelId, rendered, format));

        String finalContent = content;
        for (String linkedChannelId : channel.getLinkedChannels()) {
            channelRegistry.findById(linkedChannelId).ifPresent(linkedChannel -> {
                if (linkedChannel.isMuted()) {
                    return;
                }
                if (!permissionChecker.hasPermission(message.getSenderId(), linkedChannel.getPermission())) {
                    return;
                }
                String linkedRendered = formatter.format(linkedChannel, message, finalContent);
                deliveries.add(new ChatDelivery(linkedChannelId, linkedRendered, format));
            });
        }

        auditLogger.onMessageAllowed(message, channelId, deliveries);
        metrics.incrementAllowed(channelId);
        return ChatResult.allowed(rendered, deliveries);
    }

    /**
     * Evaluates permission and moderation checks without routing.
     */
    public boolean canSpeak(java.util.UUID playerId, String channelId) {
        ChatChannel channel = channelRegistry.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown channel: " + channelId));
        if (channel.isMuted()) {
            return false;
        }
        if (!permissionChecker.hasPermission(playerId, channel.getPermission())) {
            return false;
        }
        ChatModerationPolicy.ModerationResult moderationResult = moderationPolicy.evaluate(
                playerId,
                channelId,
                java.time.Instant.now()
        );
        return moderationResult.allowed();
    }

    public ChatHistory getHistory() {
        return history;
    }
}
