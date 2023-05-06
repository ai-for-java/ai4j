package dev.ai4j.tokenizer;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import lombok.AllArgsConstructor;

import java.util.List;

import static com.knuddels.jtokkit.api.ModelType.GPT_3_5_TURBO;

@AllArgsConstructor
public class Tokenizer {

    private static final ModelType DEFAULT_MODEL_TYPE = GPT_3_5_TURBO;
    private static final EncodingRegistry REGISTRY = Encodings.newDefaultEncodingRegistry();

    public List<Integer> encode(String text) {
        return REGISTRY.getEncodingForModel(DEFAULT_MODEL_TYPE).encodeOrdinary(text);
    }

    public List<Integer> encode(String text, int maxTokens) {
        return REGISTRY.getEncodingForModel(DEFAULT_MODEL_TYPE).encodeOrdinary(text, maxTokens).getTokens();
    }

    public String decode(List<Integer> tokens) {
        return REGISTRY.getEncodingForModel(DEFAULT_MODEL_TYPE).decode(tokens);
    }

    public int countTokens(String text) {
        return REGISTRY.getEncodingForModel(DEFAULT_MODEL_TYPE).countTokensOrdinary(text);
    }
}
