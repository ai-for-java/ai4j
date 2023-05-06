package dev.ai4j.model.chat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import dev.ai4j.model.openai.OpenAiModelName;
import lombok.Builder;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static java.util.stream.Collectors.toList;

public class OpenAiChatModel implements ChatModel {

    private static final Logger log = LoggerFactory.getLogger(OpenAiChatModel.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    private static final String OPENAI_SYSTEM_ROLE = "system";
    private static final String OPENAI_USER_ROLE = "user";
    private static final String OPENAI_ASSISTANT_ROLE = "assistant";

    private final OpenAiService openAiService;
    private final OpenAiModelName modelName;
    private final Double temperature;

    @Builder
    public OpenAiChatModel(String apiKey, OpenAiModelName modelName, Double temperature, Duration timeout) {
        this.openAiService = new OpenAiService(apiKey, timeout == null ? DEFAULT_TIMEOUT : timeout);
        this.modelName = modelName == null ? GPT_3_5_TURBO : modelName;
        this.temperature = temperature == null ? DEFAULT_TEMPERATURE : temperature;
    }

    @Override
    public MessageFromAi chat(List<ChatMessage> messages) {
        val chatCompletionRequest = ChatCompletionRequest.builder()
                .model(modelName.getId())
                .messages(toOpenAiMessages(messages))
                .temperature(temperature)
                .build();

        if (log.isDebugEnabled()) {
            val json = GSON.toJson(chatCompletionRequest);
            log.debug("Sending to OpenAI:\n{}", json);
        }

        val chatCompletionResult = openAiService.createChatCompletion(chatCompletionRequest);

        if (log.isDebugEnabled()) {
            val json = GSON.toJson(chatCompletionResult);
            log.debug("Received from OpenAI:\n{}", json);
        }

        return fromOpenAiMessage(chatCompletionResult.getChoices().get(0).getMessage());
    }

    private static List<com.theokanning.openai.completion.chat.ChatMessage> toOpenAiMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(OpenAiChatModel::toOpenAiMessage)
                .collect(toList());
    }

    private static com.theokanning.openai.completion.chat.ChatMessage toOpenAiMessage(ChatMessage message) {
        return new com.theokanning.openai.completion.chat.ChatMessage(toRole(message), message.getContents());
    }

    private static MessageFromAi fromOpenAiMessage(com.theokanning.openai.completion.chat.ChatMessage openAiChatMessage) {
        return MessageFromAi.of(openAiChatMessage.getContent()); // TODO inject token count from response
    }

    private static String toRole(ChatMessage chatMessage) {
        if (chatMessage.getClass().equals(MessageFromSystem.class)) {
            return OPENAI_SYSTEM_ROLE;
        } else if (chatMessage.getClass().equals(MessageFromHuman.class)) {
            return OPENAI_USER_ROLE;
        } else if (chatMessage.getClass().equals(MessageFromAi.class)) {
            return OPENAI_ASSISTANT_ROLE;
        }
        throw new IllegalArgumentException("Unknown message type: " + chatMessage);
    }
}
