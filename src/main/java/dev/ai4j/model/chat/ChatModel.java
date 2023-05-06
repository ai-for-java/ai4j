package dev.ai4j.model.chat;

import java.util.List;

public interface ChatModel {

    MessageFromAi chat(List<ChatMessage> messages);
}
