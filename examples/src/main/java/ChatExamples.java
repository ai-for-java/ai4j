import dev.ai4j.flows.ChatFlow;
import dev.ai4j.model.chat.ChatHistory;
import dev.ai4j.model.chat.ChatMessage;
import dev.ai4j.model.chat.MessageFromAi;
import dev.ai4j.model.chat.MessageFromHuman;
import dev.ai4j.model.chat.OpenAiChatModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;

import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.ai4j.model.openai.OpenAiModelProvider.OPEN_AI;

public class ChatExamples {

    public static class IfYouNeedSimplicity {

        public static void main(String[] args) throws IOException {

            ChatFlow chatFlow = ChatFlow.builder()
                    .modelProvider(OPEN_AI)
                    .modelName(GPT_3_5_TURBO)
                    .apiKey(System.getenv("OPENAI_API_KEY")) // https://platform.openai.com/account/api-keys
                    .messageFromSystem("You are a helpful assistant.")
                    .historyCapacityInTokens(100)
                    .temperature(0.5)
                    .build();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                System.out.print("Human: ");
                String messageFromHuman = br.readLine();

                if ("exit".equalsIgnoreCase(messageFromHuman)) {
                    return;
                }

                String messageFromAi = chatFlow.humanSaid(messageFromHuman);
                System.out.println("AI: " + messageFromAi);
            }
        }
    }

    public static class IfYouNeedMoreControl {

        public static void main(String[] args) {

            OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
                    .apiKey(System.getenv("OPENAI_API_KEY")) // https://platform.openai.com/account/api-keys
                    .modelName(GPT_3_5_TURBO)
                    .temperature(0.3)
                    .timeout(Duration.ofSeconds(120))
                    .build();

            ChatHistory chatHistory = ChatHistory.builder()
                    .messageFromSystem("You are a helpful assistant.")
                    .capacityInTokens(300)
                    .capacityInMessages(10)
                    .build();

            MessageFromHuman messageFromHuman = messageFromHuman("What does AI stand for?");

            // You have full control over the chat history.
            // You can decide if you want to add a particular message to the history.
            // You can process/modify the message before saving.
            chatHistory.add(messageFromHuman);

            List<ChatMessage> messages = chatHistory.getMessageHistory();

            MessageFromAi messageFromAi = openAiChatModel.chat(messages);

            // Here is the same as above. You have full control.
            chatHistory.add(messageFromAi);

            System.out.println(messageFromAi.getContents());
        }
    }
}
