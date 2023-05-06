package dev.ai4j.model.chat;

import dev.ai4j.tokenizer.Tokenizer;
import lombok.Getter;

@Getter
public abstract class ChatMessage {

    private static final Tokenizer TOKENIZER = new Tokenizer();
    private static final int EXTRA_TOKENS_PER_CHAT_MESSAGE = 5;

    private final String contents;
    private final int numberOfTokens;

    ChatMessage(String contents) {
        this.contents = contents;
        this.numberOfTokens = countTokens(contents); // TODO provide model
    }

    private static int countTokens(String contents) {
        // see https://jtokkit.knuddels.de/docs/getting-started/recipes/chatml
        // see https://github.com/openai/openai-cookbook/blob/main/examples/How_to_count_tokens_with_tiktoken.ipynb
        return TOKENIZER.countTokens(contents) + EXTRA_TOKENS_PER_CHAT_MESSAGE; // approximating for now
    }
}