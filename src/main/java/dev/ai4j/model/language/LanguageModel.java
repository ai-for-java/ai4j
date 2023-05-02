package dev.ai4j.model.language;

import dev.ai4j.model.language.prompt.Prompt;

public interface LanguageModel {

    String complete(Prompt prompt);

    String complete(String input);
}
