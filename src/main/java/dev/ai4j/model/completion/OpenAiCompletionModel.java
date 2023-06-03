package dev.ai4j.model.completion;

import dev.ai4j.completion.CompletionModel;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.completion.CompletionRequest;
import dev.ai4j.utils.StopWatch;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.time.Duration;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.ai4j.utils.Json.toJson;

@Slf4j
public class OpenAiCompletionModel implements CompletionModel {

    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    private final OpenAiClient client;
    private final String modelName;
    private final Double temperature;

    @Builder
    public OpenAiCompletionModel(String apiKey, String modelName, Double temperature, Duration timeout) {
        this.client = OpenAiClient.builder()
                .apiKey(apiKey)
                .timeout(timeout == null ? DEFAULT_TIMEOUT : timeout)
                .build();
        this.modelName = modelName == null ? GPT_3_5_TURBO : modelName;
        this.temperature = temperature == null ? DEFAULT_TEMPERATURE : temperature;
    }

    @Override
    public String complete(String prompt) {
        if (GPT_3_5_TURBO.equals(modelName)) { // TODO remove this
            return chatCompletion(prompt);
        } else {
            return completion(prompt);
        }
    }

    private String chatCompletion(String input) {

        val request = ChatCompletionRequest.builder()
                .model(modelName)
                .addUserMessage(input)
                .temperature(temperature)
                .build();

        if (log.isDebugEnabled()) {
            val json = toJson(request);
            log.debug("Sending to OpenAI:\n{}", json);
        }
        val sw = StopWatch.start();

        val response = client.chatCompletion(request).execute();

        val secondsElapsed = sw.secondsElapsed();
        if (log.isDebugEnabled()) {
            val json = toJson(response);
            log.debug("Received from OpenAI in {} seconds:\n{}", secondsElapsed, json);
        }

        return response.content();
    }

    private String completion(String input) {

        val request = CompletionRequest.builder()
                .model(modelName)
                .prompt(input)
                .temperature(temperature)
                .build();

        if (log.isDebugEnabled()) {
            val json = toJson(request);
            log.debug("Sending to OpenAI:\n{}", json);
        }
        val sw = StopWatch.start();

        val completionResult = client.completion(request).execute();

        val secondsElapsed = sw.secondsElapsed();
        if (log.isDebugEnabled()) {
            val json = toJson(completionResult);
            log.debug("Received from OpenAI in {} seconds:\n{}", secondsElapsed, json);
        }

        return completionResult.text();
    }
}
