package dev.ai4j.flows;

import dev.ai4j.chat.AiMessage;
import dev.ai4j.chat.ChatHistory;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.chat.ChatModel;
import lombok.Builder;

import java.util.List;

import static dev.ai4j.chat.UserMessage.userMessage;

public class ChatFlow {

    private final ChatModel chatModel;
    private final ChatHistory chatHistory;
    // TODO private final PromptTemplate promptTemplate;

    @Builder
    private ChatFlow(ChatModel chatModel, ChatHistory chatHistory) {
        this.chatModel = chatModel;
        this.chatHistory = chatHistory;
    }

    public String chat(String userMessage) {
        chatHistory.add(userMessage(userMessage));
        List<ChatMessage> history = chatHistory.history();

        AiMessage aiMessage = chatModel.chat(history);

        chatHistory.add(aiMessage);
        return aiMessage.contents();
    }
}
