package dev.ai4j.model.chat;

import dev.ai4j.prompt.Prompt;

public class MessageFromHuman extends ChatMessage {

    public MessageFromHuman(String contents) {
        super(contents);
    }

    public static MessageFromHuman of(String contents) {
        return new MessageFromHuman(contents);
    }

    public static MessageFromHuman of(Prompt prompt) {
        return new MessageFromHuman(prompt.getPrompt());
    }

    public static MessageFromHuman messageFromHuman(String contents) {
        return of(contents);
    }

    public static MessageFromHuman messageFromHuman(Prompt prompt) {
        return of(prompt.getPrompt());
    }
}
