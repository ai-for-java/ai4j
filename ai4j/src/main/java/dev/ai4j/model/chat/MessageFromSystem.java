package dev.ai4j.model.chat;

import dev.ai4j.prompt.Prompt;

public class MessageFromSystem extends ChatMessage {

    public MessageFromSystem(String contents) {
        super(contents);
    }

    public static MessageFromSystem of(String contents) {
        return new MessageFromSystem(contents);
    }

    public static MessageFromSystem of(Prompt prompt) {
        return new MessageFromSystem(prompt.getPrompt());
    }

    public static MessageFromSystem messageFromSystem(String contents) {
        return of(contents);
    }

    public static MessageFromSystem messageFromSystem(Prompt prompt) {
        return of(prompt.getPrompt());
    }
}
