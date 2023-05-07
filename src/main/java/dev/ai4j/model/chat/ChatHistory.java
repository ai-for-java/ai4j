package dev.ai4j.model.chat;

import java.util.List;

public interface ChatHistory {

    void add(ChatMessage chatMessage);

    List<ChatMessage> getHistory();
}
