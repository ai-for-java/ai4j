package dev.ai4j.model.chat;

public class MessageFromHuman extends ChatMessage {

    public MessageFromHuman(String contents) {
        super(contents);
    }

    public static MessageFromHuman of(String contents) {
        return new MessageFromHuman(contents);
    }

    public static MessageFromHuman messageFromHuman(String contents) {
        return of(contents);
    }
}
