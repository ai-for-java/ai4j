package dev.ai4j.model.chat;

import dev.ai4j.chat.ChatHistory;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.chat.SystemMessage;
import dev.ai4j.chat.UserMessage;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SimpleChatHistory implements ChatHistory {

    // safety net to limit the cost in case user did not define it himself
    private static final int DEFAULT_CAPACITY_IN_TOKENS = 200;

    private final Optional<SystemMessage> maybeMessageFromSystem;
    private final LinkedList<ChatMessage> previousMessages;
    private final Integer capacityInTokens;
    private final Integer capacityInMessages;

    private SimpleChatHistory(Builder builder) {
        this.maybeMessageFromSystem = builder.maybeSystemMessage;
        this.previousMessages = builder.previousMessages;
        this.capacityInTokens = builder.capacityInTokens;
        this.capacityInMessages = builder.capacityInMessages;
        ensureCapacity();
    }

    @Override
    public void add(ChatMessage chatMessage) {
        previousMessages.add(chatMessage);
        ensureCapacity();
    }

    @Override
    public List<ChatMessage> history() {
        val messages = new ArrayList<ChatMessage>();
        maybeMessageFromSystem.ifPresent(messages::add);
        messages.addAll(previousMessages);
        return messages;
    }

    private void ensureCapacity() {
        var currentNumberOfTokensInHistory = getCurrentNumberOfTokens();
        var currentNumberOfMessagesInHistory = getCurrentNumberOfMessages();

        while ((capacityInTokens != null && currentNumberOfTokensInHistory > capacityInTokens)
                || (capacityInMessages != null && currentNumberOfMessagesInHistory > capacityInMessages)) {

            val oldestMessage = previousMessages.removeFirst();

            // remove all mentions of human, messageFrom

            log.debug("Removing the oldest message from {} '{}' ({} tokens) to comply with capacity requirements",
                    oldestMessage instanceof UserMessage ? "user" : "AI",
                    oldestMessage.contents(),
                    oldestMessage.numberOfTokens());

            currentNumberOfTokensInHistory -= oldestMessage.numberOfTokens();
            currentNumberOfMessagesInHistory--;
        }

        log.debug("Current stats: { tokens: approximately {}, messages: {} }", getCurrentNumberOfTokens(), getCurrentNumberOfMessages());
    }

    private int getCurrentNumberOfTokens() {
        val numberOfTokensInSystemMessage = maybeMessageFromSystem.map(ChatMessage::numberOfTokens).orElse(0);
        val numberOfTokensInPreviousMessages = previousMessages.stream()
                .map(ChatMessage::numberOfTokens)
                .reduce(0, Integer::sum);
        return numberOfTokensInSystemMessage + numberOfTokensInPreviousMessages;
    }

    private int getCurrentNumberOfMessages() {
        return maybeMessageFromSystem.map(m -> 1).orElse(0) + previousMessages.size();
    }

    public static class Builder {

        private Optional<SystemMessage> maybeSystemMessage = Optional.empty();
        private Integer capacityInTokens = DEFAULT_CAPACITY_IN_TOKENS;
        private Integer capacityInMessages;
        private LinkedList<ChatMessage> previousMessages = new LinkedList<>();

        public Builder systemMessage(SystemMessage systemMessage) {
            this.maybeSystemMessage = Optional.ofNullable(systemMessage);
            return this;
        }

        public Builder systemMessage(String systemMessage) {
            if (systemMessage == null) {
                this.maybeSystemMessage = Optional.empty(); // TODO ?
                return this;
            }

            return systemMessage(SystemMessage.systemMessage(systemMessage));
        }

        public Builder capacityInTokens(Integer capacityInTokens) {
            this.capacityInTokens = capacityInTokens;
            return this;
        }

        public Builder removeCapacityRestrictionInTokens() {
            return capacityInTokens(null);
        }

        public Builder capacityInMessages(Integer capacityInMessages) {
            this.capacityInMessages = capacityInMessages;
            return this;
        }

        public Builder previousMessages(List<ChatMessage> previousMessages) {
            if (previousMessages == null) {
                return this;
            }

            this.previousMessages = new LinkedList<>(previousMessages);
            return this;
        }

        public SimpleChatHistory build() {
            return new SimpleChatHistory(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
