package dev.ai4j.model.chat.openai;

import dev.ai4j.model.chat.ChatModel;
import dev.ai4j.model.openai.OpenAiModel;
import dev.ai4j.schema.chatmessage.ChatMessage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.Builder;
import lombok.val;

import java.util.List;

import static dev.ai4j.model.openai.OpenAiModel.GPT_3_5_TURBO;
import static dev.ai4j.schema.chatmessage.ChatMessage.ChatMessageType.AI;
import static dev.ai4j.schema.chatmessage.ChatMessage.ChatMessageType.HUMAN;
import static dev.ai4j.schema.chatmessage.ChatMessage.ChatMessageType.SYSTEM;
import static java.util.stream.Collectors.toList;

public class OpenAiChatModel implements ChatModel {

    private static final double DEFAULT_TEMPERATURE = 0.7;

    private static final String OPENAI_SYSTEM_ROLE = "system";
    private static final String OPENAI_USER_ROLE = "user";
    private static final String OPENAI_ASSISTANT_ROLE = "assistant";

    private final OpenAiService openAiService;
    private final OpenAiModel model;
    private final Double temperature;

    @Builder
    public OpenAiChatModel(String apiKey, OpenAiModel model, Double temperature) {
        this.openAiService = new OpenAiService(apiKey);
        this.model = model == null ? GPT_3_5_TURBO : model;
        this.temperature = temperature == null ? DEFAULT_TEMPERATURE : temperature;
    }

    @Override
    public ChatMessage chat(List<ChatMessage> messages) {
        val chatCompletionRequest = ChatCompletionRequest.builder()
                .model(model.getModelName())
                .messages(toOpenAiMessages(messages))
                .temperature(temperature)
                .build();

        val completionResult = openAiService.createChatCompletion(chatCompletionRequest);

        return fromOpenAiMessage(completionResult.getChoices().get(0).getMessage());
    }

    private List<com.theokanning.openai.completion.chat.ChatMessage> toOpenAiMessages(List<ChatMessage> messages) {
        return messages.stream()
                .map(OpenAiChatModel::toOpenAiMessage)
                .collect(toList());
    }

    private static com.theokanning.openai.completion.chat.ChatMessage toOpenAiMessage(ChatMessage message) {
        return new com.theokanning.openai.completion.chat.ChatMessage(toRole(message.getType()), message.getContents());
    }

    private static ChatMessage fromOpenAiMessage(com.theokanning.openai.completion.chat.ChatMessage openAiChatMessage) {
        return new ChatMessage(fromOpenAiRole(openAiChatMessage.getRole()), openAiChatMessage.getContent());
    }

    private static String toRole(ChatMessage.ChatMessageType chatMessageType) {
        switch (chatMessageType) {
            case SYSTEM:
                return OPENAI_SYSTEM_ROLE;
            case HUMAN:
                return OPENAI_USER_ROLE;
            case AI:
                return OPENAI_ASSISTANT_ROLE;
            default:
                throw new IllegalArgumentException("Unknown message type: " + chatMessageType);
        }
    }

    private static ChatMessage.ChatMessageType fromOpenAiRole(String role) {
        switch (role) {
            case OPENAI_SYSTEM_ROLE:
                return SYSTEM;
            case OPENAI_USER_ROLE:
                return HUMAN;
            case OPENAI_ASSISTANT_ROLE:
                return AI;
            default:
                throw new IllegalArgumentException("Unknown message role: " + role);
        }
    }
}
