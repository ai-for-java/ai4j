package dev.ai4j.model.language.prompt;

import lombok.Value;

@Value
public class Prompt {

    String text;

    public static Prompt from(String text) {
        return new Prompt(text);
    }
}
