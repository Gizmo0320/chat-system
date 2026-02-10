package com.example.chatsystem.config;

import com.example.chatsystem.ChatChannel;

import java.util.List;
import java.util.Map;

/**
 * Parsed configuration for chat system channels.
 */
public record ChatConfig(List<ChatChannel> channels, Map<String, String> formatPresets) {}
