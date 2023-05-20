package dev.ai4j.model.chat;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import dev.ai4j.model.ModelResponseHandler;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.utils.StopWatch;
import io.reactivex.schedulers.Schedulers;
import lombok.Builder;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.ai4j.utils.Json.toJson;
import static java.util.stream.Collectors.toList;

public class OpenAiChatModel implements ChatModel {

    private static final Logger log = LoggerFactory.getLogger(OpenAiChatModel.class);

    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    private static final String OPENAI_SYSTEM_ROLE = "system";
    private static final String OPENAI_USER_ROLE = "user";
    private static final String OPENAI_ASSISTANT_ROLE = "assistant";

    private final LinkedList<OpenAiService> openAiServices;
    private final OpenAiModelName modelName;
    private final Double temperature;

    // TODO consider adding here an option for system prompt configuration

    @Builder
    public OpenAiChatModel(String apiKey,
                           Collection<String> apiKeys,
                           OpenAiModelName modelName,
                           Double temperature,
                           Duration timeout) {
        openAiServices = new LinkedList<>();
        if (apiKey != null) { // TODO check key is not null and not blank
            openAiServices.add(createOpenAiService(apiKey, timeout));
        }
        if (apiKeys != null) {
            apiKeys.forEach(key -> // TODO check key is not null and not blank
                    openAiServices.add(createOpenAiService(key, timeout)));
        }
        this.modelName = modelName == null ? GPT_3_5_TURBO : modelName;
        this.temperature = temperature == null ? DEFAULT_TEMPERATURE : temperature;
    }

    private static OpenAiService createOpenAiService(String apiKey, Duration timeout) {
        return new OpenAiService(apiKey, timeout == null ? DEFAULT_TIMEOUT : timeout);
    }

    private OpenAiService getNextOpenAiService() {
        val next = openAiServices.removeFirst();
        openAiServices.addLast(next);
        return next;
    }

    @Override
    public MessageFromAi chat(List<ChatMessage> messages) {
        val chatCompletionRequest = ChatCompletionRequest.builder()
                .model(modelName.getId())
                .messages(toOpenAiMessages(messages))
                .temperature(temperature)
                .build();

        if (log.isDebugEnabled()) {
            val json = toJson(chatCompletionRequest);
            log.debug("Sending to OpenAI:\n{}", json);
        }
        val sw = StopWatch.start();

        val chatCompletionResult = getNextOpenAiService().createChatCompletion(chatCompletionRequest);

        val secondsElapsed = sw.secondsElapsed();
        if (log.isDebugEnabled()) {
            val json = toJson(chatCompletionResult);
            log.debug("Received from OpenAI in {} seconds:\n{}", secondsElapsed, json);
        }

        return fromOpenAiMessage(chatCompletionResult.getChoices().get(0).getMessage());
    }

    @Override
    public void chat(List<ChatMessage> messages, ModelResponseHandler modelResponseHandler) {
        val chatCompletionRequest = ChatCompletionRequest.builder()
                .model(modelName.getId())
                .messages(toOpenAiMessages(messages))
                .temperature(temperature)
                .stream(true)
                .build();

        val responseBuilder = new StringBuilder();
        getNextOpenAiService().streamChatCompletion(chatCompletionRequest)
                .observeOn(Schedulers.io()) // TODO computation? something else?
                .subscribe(chunk -> {
                            val fragment = chunk.getChoices().get(0).getMessage().getContent();
                            log.debug("Received from OpenAI: '{}'", fragment);
                            responseBuilder.append(fragment);
                            modelResponseHandler.handleResponseFragment(fragment);
                        },
                        error -> {
                            log.error("", error);
                            modelResponseHandler.handleError(error);
                        },
                        () -> {
                            val completeResponse = responseBuilder.toString();
                            log.debug("Received from OpenAI: '{}'", completeResponse);
                            modelResponseHandler.handleCompleteResponse(completeResponse);
                        });
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
