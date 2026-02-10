package com.example.chatsystem.routing;

/**
 * Rendered delivery target for a channel.
 */
public record ChatDelivery(String channelId, String renderedMessage, ChatFormat format) {}
