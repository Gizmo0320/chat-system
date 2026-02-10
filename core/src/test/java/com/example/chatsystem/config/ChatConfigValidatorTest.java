package com.example.chatsystem.config;

import com.example.chatsystem.ChatChannel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChatConfigValidatorTest {
    private final ChatConfigValidator validator = new ChatConfigValidator();

    @Test
    void validatesLinkedChannelsAndPresets() {
        ChatChannel global = new ChatChannel("global", "Global", "perm.global", "{message}", "default", false,
                ChatChannel.RelayMode.LOCAL, List.of("staff"));
        ChatChannel staff = new ChatChannel("staff", "Staff", "perm.staff", "{message}", null, false,
                ChatChannel.RelayMode.LOCAL, List.of());
        ChatConfig config = new ChatConfig(List.of(global, staff), Map.of("default", "[G] {message}"));

        assertDoesNotThrow(() -> validator.validate(config));
    }

    @Test
    void rejectsUnknownLinks() {
        ChatChannel global = new ChatChannel("global", "Global", "perm.global", "{message}", null, false,
                ChatChannel.RelayMode.LOCAL, List.of("missing"));
        ChatConfig config = new ChatConfig(List.of(global), Map.of());

        assertThrows(IllegalArgumentException.class, () -> validator.validate(config));
    }
}
