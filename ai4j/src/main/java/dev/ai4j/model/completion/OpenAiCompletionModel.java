package dev.ai4j.model.completion;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import dev.ai4j.model.completion.structured.Description;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.prompt.Prompt;
import dev.ai4j.prompt.PromptTemplate;
import dev.ai4j.utils.Json;
import dev.ai4j.utils.StopWatch;
import lombok.Builder;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.ai4j.utils.Json.*;
import static java.util.stream.Collectors.toList;

public class OpenAiCompletionModel implements CompletionModel {

    private static final Logger log = LoggerFactory.getLogger(OpenAiCompletionModel.class);

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
        return complete(prompt.getPrompt());
    }

    @Override
    public String complete(String input) {
        if (GPT_3_5_TURBO.getId().equals(modelName.getId())) {
            return chatCompletion(input);
        } else {
            return completion(input);
        }
    }

    @Override
    public <S> S getOne(Class<S> structured) {
        return getMultiple(structured, 1).get(0);
    }

    @Override
    public <S> List<S> getMultiple(Class<S> structured, int n) {
        val description = structured.getAnnotation(Description.class).value();
        val jsonStructure = generateJsonStructure(structured);
        val maybeJsonExample = generateJsonExample(structured);

        val promptTemplate = PromptTemplate.from(
                "Provide exactly {{number_of_examples}} example(s) of {{description}} in exactly following JSON format:\n" +
                        "{{json_structure}}\n" +
                        "\n" +
                        "{{maybe_example}}" +
                        "Do not provide any other information, just valid JSON object(s)!"
        );

        val params = new HashMap<String, Object>();
        params.put("number_of_examples", n);
        params.put("description", description);
        params.put("json_structure", jsonStructure);
        params.put("maybe_example", maybeJsonExample
                .map(example -> String.format("For example:\n%s\n\n", example))
                .orElse(""));

        val completion = complete(promptTemplate.apply(params));

        val jsonElements = parse(completion);

        return jsonElements.stream()
                .map(jsonElement -> {
                    try {
                        return Json.fromJson(jsonElement.toString(), structured);
                    } catch (Exception e) {
                        // TODO
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .limit(n)
                .collect(toList());
    }

    private static ArrayList<JsonElement> parse(String completion) {
        val reader = new JsonReader(new StringReader(completion));
        reader.setLenient(true);

        val jsonElements = new ArrayList<JsonElement>();
        try {
            while (reader.peek() != JsonToken.END_DOCUMENT) {
                jsonElements.add(JsonParser.parseReader(reader));
            }
        } catch (Exception e) {
            // TODO
        }

        return jsonElements;
    }

    // TODO remove duplication with OpenAiChatModel
    private String chatCompletion(String input) {
        val chatCompletionRequest = ChatCompletionRequest.builder()
                .model(modelName.getId())
                .messages(ImmutableList.of(new ChatMessage("user", input)))
                .temperature(temperature)
                .build();

        if (log.isDebugEnabled()) {
            val json = toJson(chatCompletionRequest);
            log.debug("Sending to OpenAI:\n{}", json);
        }
        val sw = StopWatch.start();

        val chatCompletionResult = openAiService.createChatCompletion(chatCompletionRequest);

        val secondsElapsed = sw.secondsElapsed();
        if (log.isDebugEnabled()) {
            val json = toJson(chatCompletionResult);
            log.debug("Received from OpenAI in {} seconds:\n{}", secondsElapsed, json);
        }

        return chatCompletionResult.getChoices().get(0).getMessage().getContent();
    }

    private String completion(String input) {
        val completionRequest = CompletionRequest.builder()
                .model(modelName.getId())
                .prompt(input)
                .temperature(temperature)
                .build();

        if (log.isDebugEnabled()) {
            val json = toJson(completionRequest);
            log.debug("Sending to OpenAI:\n{}", json);
        }
        val sw = StopWatch.start();

        val completionResult = openAiService.createCompletion(completionRequest);

        val secondsElapsed = sw.secondsElapsed();
        if (log.isDebugEnabled()) {
            val json = toJson(completionResult);
            log.debug("Received from OpenAI in {} seconds:\n{}", secondsElapsed, json);
        }

        return completionResult.getChoices().get(0).getText();
    }
}
