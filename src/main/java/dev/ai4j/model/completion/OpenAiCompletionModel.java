package dev.ai4j.model.completion;

import com.google.common.collect.ImmutableList;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import dev.ai4j.model.completion.CompletionModel;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.prompt.Prompt;
import lombok.Builder;
import lombok.val;

import java.time.Duration;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

public class OpenAiCompletionModel implements CompletionModel {

    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    private final OpenAiService openAiService;
    private final OpenAiModelName modelName;
    private final Double temperature;

    @Builder
    public OpenAiCompletionModel(String apiKey, OpenAiModelName modelName, Double temperature, Duration timeout) {
        this.openAiService = new OpenAiService(apiKey, timeout == null ? DEFAULT_TIMEOUT : timeout);
        this.modelName = modelName == null ? GPT_3_5_TURBO : modelName;
        this.temperature = temperature == null ? DEFAULT_TEMPERATURE : temperature;
    }

    @Override
    public String complete(Prompt prompt) {
        return complete(prompt.getText());
    }

    @Override
    public String complete(String input) {
        if (GPT_3_5_TURBO.getId().equals(modelName.getId())) {
            return chatCompletion(input);
        } else {
            return completion(input);
        }
    }

    // TODO remove duplication with OpenAiChatModel
    private String chatCompletion(String input) {
        val chatCompletionRequest = ChatCompletionRequest.builder()
                .model(modelName.getId())
                .messages(ImmutableList.of(new ChatMessage("user", input)))
                .temperature(temperature)
                .build();

        val chatCompletionResult = openAiService.createChatCompletion(chatCompletionRequest);

        return chatCompletionResult.getChoices().get(0).getMessage().getContent();
    }

    private String completion(String input) {
        val completionRequest = CompletionRequest.builder()
                .model(modelName.getId())
                .prompt(input)
                .temperature(temperature)
                .build();

        val completionResult = openAiService.createCompletion(completionRequest);

        return completionResult.getChoices().get(0).getText();
    }
}
