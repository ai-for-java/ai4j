package dev.ai4j.model.chat;

public class MessageFromAi extends ChatMessage {

    public MessageFromAi(String contents) {
        super(contents);
    }

    public static MessageFromAi of(String contents) {
        return new MessageFromAi(contents);
    }

    public static MessageFromAi messageFromAi(String contents) {
        return of(contents);
    }
}
