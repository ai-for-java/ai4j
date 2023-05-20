package dev.ai4j.model.openai;

import dev.ai4j.model.ModelProvider;
import lombok.Value;

@Value
public class OpenAiModelProvider implements ModelProvider {

    public static final OpenAiModelProvider OPEN_AI = new OpenAiModelProvider("openai");

    String id;
}
