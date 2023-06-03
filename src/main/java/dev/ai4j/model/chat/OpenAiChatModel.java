package dev.ai4j.model.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import dev.ai4j.PromptTemplate;
import dev.ai4j.StreamingResponseHandler;
import dev.ai4j.chat.AiMessage;
import dev.ai4j.chat.ChatMessage;
import dev.ai4j.chat.ChatModel;
import dev.ai4j.chat.UserMessage;
import dev.ai4j.model.completion.structured.Description;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.chat.Message;
import dev.ai4j.openai4j.chat.Role;
import dev.ai4j.utils.Json;
import dev.ai4j.utils.StopWatch;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.io.StringReader;
import java.time.Duration;
import java.util.*;

import static dev.ai4j.chat.AiMessage.aiMessage;
import static dev.ai4j.chat.UserMessage.userMessage;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.ai4j.openai4j.chat.Role.*;
import static dev.ai4j.utils.Json.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

@Slf4j
public class OpenAiChatModel implements ChatModel { // TODO all models in one "service"?

    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    private final OpenAiClient client;
    private final String modelName;
    private final Double temperature;

    // TODO consider adding here an option for system prompt configuration

    @Builder
    public OpenAiChatModel(String apiKey,
                           String modelName,
                           Double temperature,
                           Duration timeout) {
        this.client = OpenAiClient.builder()
                .apiKey(apiKey)
                .timeout(timeout == null ? DEFAULT_TIMEOUT : timeout)
                .build();
        this.modelName = modelName == null ? GPT_3_5_TURBO : modelName;
        this.temperature = temperature == null ? DEFAULT_TEMPERATURE : temperature;
    }

    @Override
    public AiMessage chat(List<ChatMessage> messages) {

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelName)
                .messages(toOpenAiMessages(messages))
                .temperature(temperature)
                .build();

        if (log.isDebugEnabled()) {
            String json = toJson(request);
            log.debug("Sending to OpenAI:\n{}", json);
        }
        StopWatch sw = StopWatch.start();

        ChatCompletionResponse response = client.chatCompletion(request).execute();

        long secondsElapsed = sw.secondsElapsed();
        if (log.isDebugEnabled()) {
            String json = toJson(response);
            log.debug("Received from OpenAI in {} seconds:\n{}", secondsElapsed, json);
        }

        return aiMessage(response.content());
    }

    @Override
    public AiMessage chat(ChatMessage... messages) {
        return chat(asList(messages));
    }

    @Override
    public String chat(String userMessage) {
        AiMessage aiMessage = chat(userMessage(userMessage));
        return aiMessage.contents();
    }

    @Override
    public void chat(List<ChatMessage> messages, StreamingResponseHandler handler) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelName)
                .messages(toOpenAiMessages(messages))
                .temperature(temperature)
                .stream(true)
                .build();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    String content = partialResponse.choices().get(0).delta().content();
                    if (content != null) {
                        handler.onPartialResponse(content);
                    }
                })
                .onComplete(handler::onComplete)
                .onError(handler::onError)
                .execute();
    }

    private static List<Message> toOpenAiMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(OpenAiChatModel::toOpenAiMessage)
                .collect(toList());
    }

    private static Message toOpenAiMessage(ChatMessage message) {
        Role role;

        if (message instanceof UserMessage) {
            role = USER;
        } else if (message instanceof AiMessage) {
            role = ASSISTANT;
        } else {
            role = SYSTEM;
        }

        return Message.builder()
                .role(role)
                .content(message.contents())
                .build();
    }

    @Override
    public <S> S getOne(Class<S> structured) {
        return getMultiple(structured, 1).get(0);
    }

    @Override
    public <S> List<S> getMultiple(Class<S> structured, int n) {
        String description = structured.getAnnotation(Description.class).value();
        String jsonStructure = generateJsonStructure(structured);
        Optional<String> maybeJsonExample = generateJsonExample(structured);

        PromptTemplate promptTemplate = PromptTemplate.from(
                "Provide exactly {{number_of_examples}} example(s) of {{description}} in exactly following JSON format:\n" +
                        "{{json_structure}}\n" +
                        "\n" +
                        "{{maybe_example}}" +
                        "Do not provide any other information, just valid JSON object(s)!"
        );

        Map<String, Object> params = new HashMap<>();
        params.put("number_of_examples", n);
        params.put("description", description);
        params.put("json_structure", jsonStructure);
        params.put("maybe_example", maybeJsonExample
                .map(example -> String.format("For example:\n%s\n\n", example))
                .orElse(""));

        String prompt = promptTemplate.format(params);

        AiMessage aiMessage = chat(userMessage(prompt));

        val jsonElements = parse(aiMessage.contents());

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

    private static ArrayList<JsonElement> parse(String json) {
        val reader = new JsonReader(new StringReader(json));
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
}
