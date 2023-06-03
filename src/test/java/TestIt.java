import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.chat.ChatCompletionResponse;
import org.junit.jupiter.api.Test;

import static dev.ai4j.openai4j.Model.GPT_4;

public class TestIt {

    @Test
    void test() {

        OpenAiClient client = new OpenAiClient(System.getenv("OPENAI_API_KEY"));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(GPT_4)
                .addSystemMessage("You are a helpful assistant")
                .addUserMessage("Tell me a joke")
                .temperature(0.7)
                .build();

        ChatCompletionResponse response = client.chatCompletion(request).execute();
        System.out.println(response.content());
    }
}
