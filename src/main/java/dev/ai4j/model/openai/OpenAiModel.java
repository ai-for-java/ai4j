package dev.ai4j.model.openai;

import lombok.Value;

@Value
public class OpenAiModel {

    public static final OpenAiModel TEXT_EMBEDDING_ADA_002 = from("text-embedding-ada-002");
    public static final OpenAiModel GPT_3_5_TURBO = from("gpt-3.5-turbo");

    String modelName;

    public static OpenAiModel from(String modelName) {
        return new OpenAiModel(modelName);
    }
}
