package com.example.chatsystem.filters;

import com.example.chatsystem.ChatMessage;

/**
 * Intercepts chat messages to allow, deny, or replace their content.
 */
public interface ChatFilter {
    FilterResult apply(ChatMessage message);

    /**
     * Result returned by a filter.
     *
     * @param allowed whether the message should proceed
     * @param reason optional denial reason
     * @param replacementContent optional replacement payload
     */
    record FilterResult(boolean allowed, String reason, String replacementContent) {
        public static FilterResult allow() {
            return new FilterResult(true, null, null);
        }

        public static FilterResult deny(String reason) {
            return new FilterResult(false, reason, null);
        }

        public static FilterResult replace(String replacementContent) {
            return new FilterResult(true, null, replacementContent);
        }
    }
}
