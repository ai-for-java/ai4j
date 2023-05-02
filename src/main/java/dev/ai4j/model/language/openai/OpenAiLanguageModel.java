package dev.ai4j.model.language.openai;

import com.google.common.collect.ImmutableList;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import dev.ai4j.model.language.LanguageModel;
import dev.ai4j.model.language.prompt.Prompt;
import dev.ai4j.model.openai.OpenAiModel;
import lombok.Builder;
import lombok.val;

import java.time.Duration;

public class OpenAiLanguageModel implements LanguageModel {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
    private static final double DEFAULT_TEMPERATURE = 0.7;

    private final OpenAiService openAiService;
    private final OpenAiModel model;
    private final Double temperature;

    @Builder
    public OpenAiLanguageModel(String apiKey, OpenAiModel model, Double temperature, Duration timeout) {
        this.openAiService = new OpenAiService(apiKey, timeout == null ? DEFAULT_TIMEOUT : timeout);
        this.model = model == null ? OpenAiModel.GPT_3_5_TURBO : model;
        this.temperature = temperature == null ? DEFAULT_TEMPERATURE : temperature;
    }

    @Override
    public String complete(Prompt prompt) {
        return complete(prompt.getText());
    }

    @Override
    public String complete(String input) {
        if (OpenAiModel.GPT_3_5_TURBO.getModelName().equals(model.getModelName())) {
            return chatCompletion(input);
        } else {
            return completion(input);
        }
    }

    private String chatCompletion(String input) {
        val chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model.getModelName())
                .messages(ImmutableList.of(new ChatMessage("user", input)))
                .temperature(temperature)
                .build();

        val completionResult = openAiService.createChatCompletion(chatCompletionRequest);

        return completionResult.getChoices().get(0).getMessage().getContent();
    }

    private String completion(String input) {
        val completionRequest = CompletionRequest.builder()
                .model(model.getModelName())
                .prompt(input)
                .temperature(temperature)
                .build();

        val completionResult = openAiService.createCompletion(completionRequest);

        return completionResult.getChoices().get(0).getText();
    }
}
