package dev.ai4j.model.completion;

import dev.ai4j.prompt.Prompt;

import java.util.List;

public interface CompletionModel {

    String complete(Prompt prompt);

    String complete(String prompt);

    <S> S getOne(Class<S> structured);

    <S> List<S> getMultiple(Class<S> structured, int n);
}
