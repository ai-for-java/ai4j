package dev.ai4j.model.chat;

import dev.ai4j.model.ModelResponseHandler;

import java.util.List;

public interface ChatModel {

    MessageFromAi chat(List<ChatMessage> messages);

    void chat(List<ChatMessage> messages, ModelResponseHandler responseHandler); // TODO stream? async?
}
