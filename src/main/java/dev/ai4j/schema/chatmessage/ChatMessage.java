package dev.ai4j.schema.chatmessage;

import lombok.Value;

import static dev.ai4j.schema.chatmessage.ChatMessage.ChatMessageType.AI;
import static dev.ai4j.schema.chatmessage.ChatMessage.ChatMessageType.HUMAN;
import static dev.ai4j.schema.chatmessage.ChatMessage.ChatMessageType.SYSTEM;

@Value
public class ChatMessage {

    ChatMessageType type;
    String contents;

    public static ChatMessage fromSystem(String contents) {
        return new ChatMessage(SYSTEM, contents);
    }

    public static ChatMessage fromHuman(String contents) {
        return new ChatMessage(HUMAN, contents);
    }

    public static ChatMessage fromAi(String contents) {
        return new ChatMessage(AI, contents);
    }

    public enum ChatMessageType {
        SYSTEM, HUMAN, AI
    }
}