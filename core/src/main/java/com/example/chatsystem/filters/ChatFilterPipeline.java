package com.example.chatsystem.filters;

import com.example.chatsystem.ChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Applies a sequence of filters and captures filter decisions for diagnostics.
 */
public final class ChatFilterPipeline {
    private final List<ChatFilter> filters;

    private ChatFilterPipeline(List<ChatFilter> filters) {
        this.filters = List.copyOf(filters);
    }

    public FilterResult apply(ChatMessage message) {
        String content = message.getContent();
        List<FilterDecision> decisions = new ArrayList<>();
        for (ChatFilter filter : filters) {
            ChatFilter.FilterResult result = filter.apply(message);
            decisions.add(new FilterDecision(
                    filter.getClass().getSimpleName(),
                    result.allowed(),
                    result.reason(),
                    result.replacementContent()
            ));
            if (!result.allowed()) {
                return FilterResult.denied(result.reason(), content, decisions);
            }
            if (result.replacementContent() != null) {
                content = result.replacementContent();
            }
        }
        return FilterResult.allowed(content, decisions);
    }

    public List<ChatFilter> getFilters() {
        return filters;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final List<ChatFilter> filters = new ArrayList<>();

        public Builder add(ChatFilter filter) {
            filters.add(Objects.requireNonNull(filter, "filter"));
            return this;
        }

        public Builder addAll(List<ChatFilter> filters) {
            Objects.requireNonNull(filters, "filters");
            filters.forEach(this::add);
            return this;
        }

        public ChatFilterPipeline build() {
            return new ChatFilterPipeline(filters);
        }
    }

    public record FilterDecision(String filterName, boolean allowed, String reason, String replacementContent) {}

    public record FilterResult(boolean allowed, String reason, String content, List<FilterDecision> decisions) {
        public static FilterResult allowed(String content, List<FilterDecision> decisions) {
            return new FilterResult(true, null, content, Collections.unmodifiableList(new ArrayList<>(decisions)));
        }

        public static FilterResult denied(String reason, String content, List<FilterDecision> decisions) {
            return new FilterResult(false, reason, content, Collections.unmodifiableList(new ArrayList<>(decisions)));
        }
    }
}
