package dev.ai4j.model.chat;

import dev.ai4j.schema.chatmessage.ChatMessage;

import java.util.List;

public interface ChatModel {

    ChatMessage chat(List<ChatMessage> messages);
}
