package dev.ai4j.model.chat;

import lombok.val;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SimpleChatHistory implements ChatHistory {

    public static final Logger log = LoggerFactory.getLogger(SimpleChatHistory.class);

    // safety net to limit the cost in case user did not define it himself
    private static final int DEFAULT_CAPACITY_IN_TOKENS = 200;

    private final Optional<MessageFromSystem> maybeMessageFromSystem;
    private final LinkedList<ChatMessage> previousMessages;
    private final Integer capacityInTokens;
    private final Integer capacityInMessages;

    private SimpleChatHistory(Builder builder) {
        this.maybeMessageFromSystem = builder.maybeMessageFromSystem;
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
    public List<ChatMessage> getHistory() {
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

            log.debug("Removing the oldest message from {} '{}' ({} tokens) to comply with capacity requirements",
                    oldestMessage instanceof MessageFromHuman ? "human" : "AI",
                    oldestMessage.getContents(),
                    oldestMessage.getNumberOfTokens());

            currentNumberOfTokensInHistory -= oldestMessage.getNumberOfTokens();
            currentNumberOfMessagesInHistory--;
        }

        log.debug("Current stats: { tokens: approximately {}, messages: {} }", getCurrentNumberOfTokens(), getCurrentNumberOfMessages());
    }

    private int getCurrentNumberOfTokens() {
        val numberOfTokensInSystemMessage = maybeMessageFromSystem.map(ChatMessage::getNumberOfTokens).orElse(0);
        val numberOfTokensInPreviousMessages = previousMessages.stream()
                .map(ChatMessage::getNumberOfTokens)
                .reduce(0, Integer::sum);
        return numberOfTokensInSystemMessage + numberOfTokensInPreviousMessages;
    }

    private int getCurrentNumberOfMessages() {
        return maybeMessageFromSystem.map(m -> 1).orElse(0) + previousMessages.size();
    }

    public static class Builder {

        private Optional<MessageFromSystem> maybeMessageFromSystem = Optional.empty();
        private Integer capacityInTokens = DEFAULT_CAPACITY_IN_TOKENS;
        private Integer capacityInMessages;
        private LinkedList<ChatMessage> previousMessages = new LinkedList<>();

        public Builder messageFromSystem(MessageFromSystem messageFromSystem) {
            this.maybeMessageFromSystem = Optional.ofNullable(messageFromSystem);
            return this;
        }

        public Builder messageFromSystem(String messageFromSystem) {
            if (messageFromSystem == null) {
                this.maybeMessageFromSystem = Optional.empty();
                return this;
            }

            return messageFromSystem(MessageFromSystem.of(messageFromSystem));
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
