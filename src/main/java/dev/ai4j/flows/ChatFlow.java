package dev.ai4j.flows;

import dev.ai4j.model.ModelName;
import dev.ai4j.model.ModelProvider;
import dev.ai4j.model.chat.ChatHistory;
import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.ChatModel;
import dev.ai4j.model.chat.MessageFromHuman;
import dev.ai4j.model.chat.OpenAiChatModel;
import dev.ai4j.model.openai.OpenAiModelName;
import lombok.val;

import java.time.Duration;
import java.util.List;

public class ChatFlow {

    private final ChatModel chatModel;
    private final ChatHistory chatHistory;

    private ChatFlow(Builder builder) {

        // for now, only OpenAI provider is supported
        this.chatModel = OpenAiChatModel.builder()
                .apiKey(builder.apiKey)
                .modelName((OpenAiModelName) builder.modelName)
                .temperature(builder.temperature)
                .timeout(builder.timeout)
                .build();

        this.chatHistory = ChatHistory.builder()
                .messageFromSystem(builder.messageFromSystem)
                .previousMessages(builder.previousMessages)
                .capacityInTokens(builder.historyCapacityInTokens)
                .capacityInMessages(builder.historyCapacityInMessages)
                .build();
    }

    public String humanSaid(String humanMessage) {
        chatHistory.add(MessageFromHuman.messageFromHuman(humanMessage));
        val messages = chatHistory.getMessageHistory();

        val aiMessage = chatModel.chat(messages);

        chatHistory.add(aiMessage);
        return aiMessage.getContents();
    }

    public static class Builder {

        private ModelProvider modelProvider;
        private ModelName modelName;
        private String apiKey;
        private Double temperature;
        private Duration timeout;
        private String messageFromSystem;
        private List<ChatMessage> previousMessages;
        private Integer historyCapacityInTokens;
        private Integer historyCapacityInMessages;

        public Builder modelProvider(ModelProvider modelProvider) {
            this.modelProvider = modelProvider;
            return this;
        }

        public Builder modelName(ModelName modelName) {
            this.modelName = modelName;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder timeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder messageFromSystem(String messageFromSystem) {
            this.messageFromSystem = messageFromSystem;
            return this;
        }

        public Builder previousMessages(List<ChatMessage> previousMessages) {
            this.previousMessages = previousMessages;
            return this;
        }

        public Builder historyCapacityInTokens(Integer historyCapacityInTokens) {
            this.historyCapacityInTokens = historyCapacityInTokens;
            return this;
        }

        public Builder historyCapacityInMessages(Integer historyCapacityInMessages) {
            this.historyCapacityInMessages = historyCapacityInMessages;
            return this;
        }

        public ChatFlow build() {
            return new ChatFlow(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
