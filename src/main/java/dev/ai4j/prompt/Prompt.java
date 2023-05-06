package dev.ai4j.prompt;

import lombok.Value;

@Value
public class Prompt {

    String text;

    public static Prompt from(String text) {
        return new Prompt(text);
    }
}
