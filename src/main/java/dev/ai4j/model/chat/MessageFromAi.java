package dev.ai4j.model.chat;

import dev.ai4j.prompt.Prompt;

public class MessageFromAi extends ChatMessage {

    public MessageFromAi(String contents) {
        super(contents);
    }

    public static MessageFromAi of(String contents) {
        return new MessageFromAi(contents);
    }

    public static MessageFromAi of(Prompt prompt) {
        return new MessageFromAi(prompt.getPrompt());
    }

    public static MessageFromAi messageFromAi(String contents) {
        return of(contents);
    }

    public static MessageFromAi messageFromAi(Prompt prompt) {
        return of(prompt.getPrompt());
    }
}
