package com.example.chatsystem.filters;

import com.example.chatsystem.ChatMessage;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Simple profanity filter that replaces banned words with a placeholder.
 */
public final class ProfanityFilter implements ChatFilter {
    private final List<String> bannedWords;

    public ProfanityFilter(List<String> bannedWords) {
        this.bannedWords = List.copyOf(Objects.requireNonNull(bannedWords, "bannedWords"));
    }

    @Override
    public FilterResult apply(ChatMessage message) {
        String content = message.getContent();
        String lowered = content.toLowerCase(Locale.ROOT);
        for (String banned : bannedWords) {
            if (lowered.contains(banned.toLowerCase(Locale.ROOT))) {
                String replacement = lowered.replace(banned.toLowerCase(Locale.ROOT), "****");
                return FilterResult.replace(replacement);
            }
        }
        return FilterResult.allow();
    }
}
