package dev.ai4j.flows;

import dev.ai4j.model.chat.ChatHistory;
import dev.ai4j.model.chat.ChatModel;
import lombok.Builder;
import lombok.val;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;

public class ChatFlow {

    private final ChatModel chatModel;
    private final ChatHistory chatHistory;

    @Builder
    private ChatFlow(ChatModel chatModel, ChatHistory chatHistory) {
        this.chatModel = chatModel;
        this.chatHistory = chatHistory;
    }

    public String humanSaid(String humanMessage) {
        chatHistory.add(messageFromHuman(humanMessage));
        val history = chatHistory.getHistory();

        val messageFromAi = chatModel.chat(history);

        chatHistory.add(messageFromAi);
        return messageFromAi.getContents();
    }
}
