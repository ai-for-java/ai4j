package dev.ai4j.model.openai;

import dev.ai4j.model.ModelName;
import lombok.Value;

@Value
public class OpenAiModelName implements ModelName {

    public static final OpenAiModelName GPT_3_5_TURBO = from("gpt-3.5-turbo");
    public static final OpenAiModelName GPT_4 = from("gpt-4");
    public static final OpenAiModelName GPT_4_32K = from("gpt-4-32k");
    public static final OpenAiModelName CODE_DAVINCI_002 = from("code-davinci-002");
    public static final OpenAiModelName TEXT_DAVINCI_002 = from("text-davinci-002");
    public static final OpenAiModelName TEXT_DAVINCI_003 = from("text-davinci-003");
    public static final OpenAiModelName TEXT_EMBEDDING_ADA_002 = from("text-embedding-ada-002");

    String id;

    public static OpenAiModelName from(String modelName) {
        return new OpenAiModelName(modelName);
    }
}
