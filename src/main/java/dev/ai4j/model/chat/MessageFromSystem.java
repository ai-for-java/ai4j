package dev.ai4j.model.chat;

public class MessageFromSystem extends ChatMessage {

    public MessageFromSystem(String contents) {
        super(contents);
    }

    public static MessageFromSystem of(String contents) {
        return new MessageFromSystem(contents);
    }

    public static MessageFromSystem messageFromSystem(String contents) {
        return of(contents);
    }
}
