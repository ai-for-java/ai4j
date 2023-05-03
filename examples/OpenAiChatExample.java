import dev.ai4j.model.chat.openai.OpenAiChatModel;
import dev.ai4j.model.openai.OpenAiModel;
import dev.ai4j.schema.chatmessage.ChatMessage;
import dev.ai4j.utils.Utils;

import java.util.List;

public class OpenAiChatExample {

    public static void main(String[] args) {

        OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .model(OpenAiModel.GPT_3_5_TURBO)
                .build();

        List<ChatMessage> messages = Utils.list(
                ChatMessage.fromSystem("You are a professional German translator. Translate each message from a user."),
                ChatMessage.fromHuman("Hello, how are you doing?")
        );

        ChatMessage aiMessage = openAiChatModel.chat(messages);

        System.out.println(aiMessage.getContents());
    }
}
