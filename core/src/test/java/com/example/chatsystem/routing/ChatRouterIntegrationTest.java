package com.example.chatsystem.routing;

import com.example.chatsystem.ChatChannel;
import com.example.chatsystem.ChatMessage;
import com.example.chatsystem.ChatSystem;
import com.example.chatsystem.audit.NoopChatAuditLogger;
import com.example.chatsystem.filters.ChatFilterPipeline;
import com.example.chatsystem.formatting.PlainFormatter;
import com.example.chatsystem.moderation.AllowAllModerationPolicy;
import com.example.chatsystem.observability.InMemoryChatMetrics;
import com.example.chatsystem.permissions.AllowAllPermissionChecker;
import com.example.chatsystem.storage.InMemoryChatHistory;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatRouterIntegrationTest {
    @Test
    void mutedChannelDeniedAndMetricsIncremented() {
        InMemoryChatMetrics metrics = new InMemoryChatMetrics();
        ChatSystem system = ChatSystem.builder()
                .addChannel(new ChatChannel("global", "Global", "perm.global", "{player}: {message}", null, true,
                        ChatChannel.RelayMode.LOCAL, List.of()))
                .filterPipeline(ChatFilterPipeline.builder().build())
                .formatter(new PlainFormatter())
                .moderationPolicy(new AllowAllModerationPolicy())
                .permissionChecker(new AllowAllPermissionChecker())
                .history(new InMemoryChatHistory(20))
                .auditLogger(new NoopChatAuditLogger())
                .metrics(metrics)
                .build();

        ChatResult result = system.send("global", new ChatMessage(UUID.randomUUID(), "Player", "hello", Instant.now(), "paper"));

        assertFalse(result.allowed());
        assertTrue(metrics.getDeniedByReason().keySet().stream().anyMatch(k -> k.startsWith("global|")));
    }
}
