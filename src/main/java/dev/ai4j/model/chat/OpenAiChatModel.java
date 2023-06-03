package dev.ai4j.model.chat;

import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import dev.ai4j.openai4j.chat.Message;
import dev.ai4j.openai4j.chat.Role;
import dev.ai4j.utils.StopWatch;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

import static dev.ai4j.model.chat.MessageFromAi.messageFromAi;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.ai4j.openai4j.chat.Role.*;
import static dev.ai4j.utils.Json.toJson;
import static java.util.stream.Collectors.toList;

@Slf4j
public class OpenAiChatModel implements ChatModel { // TODO all models in one "service"?

    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    private final OpenAiClient client;
    private final OpenAiModelName modelName;
    private final Double temperature;

    // TODO consider adding here an option for system prompt configuration

    @Builder
    public OpenAiChatModel(String apiKey,
                           OpenAiModelName modelName,
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
    public MessageFromAi chat(List<ChatMessage> messages) {

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelName.getId())
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

        return messageFromAi(response.content());
    }

    @Override
    public void chat(List<ChatMessage> messages, ModelResponseHandler responseHandler) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(modelName.getId())
                .messages(toOpenAiMessages(messages))
                .temperature(temperature)
                .stream(true)
                .build();

        client.chatCompletion(request)
                .onPartialResponse(partialResponse -> {
                    responseHandler.handleResponseFragment(partialResponse.choices().get(0).delta().content());
                })
                .onComplete(responseHandler::handleResponseCompletion)
                .onError(responseHandler::handleError)
                .execute();
    }

    private static List<Message> toOpenAiMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(OpenAiChatModel::toOpenAiMessage)
                .collect(toList());
    }

    private static Message toOpenAiMessage(ChatMessage message) {
        Role role;

        if (message instanceof MessageFromHuman) {
            role = USER;
        } else if (message instanceof MessageFromAi) {
            role = ASSISTANT;
        } else {
            role = SYSTEM;
        }

        return Message.builder()
                .role(role)
                .content(message.getContents())
                .build();
    }
}
