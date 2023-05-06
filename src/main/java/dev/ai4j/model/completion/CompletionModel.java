package dev.ai4j.model.completion;

import dev.ai4j.prompt.Prompt;

public interface CompletionModel {

    String complete(Prompt prompt);

    String complete(String prompt);
}
