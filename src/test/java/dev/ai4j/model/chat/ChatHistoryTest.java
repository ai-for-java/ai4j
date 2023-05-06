package dev.ai4j.model.chat;

import lombok.val;
import org.junit.jupiter.api.Test;

import static dev.ai4j.model.chat.MessageFromAi.messageFromAi;
import static dev.ai4j.model.chat.MessageFromHuman.messageFromHuman;
import static dev.ai4j.model.chat.MessageFromSystem.messageFromSystem;
import static dev.ai4j.utils.TestUtils.messageFromAiWithTokens;
import static dev.ai4j.utils.TestUtils.messageFromHumanWithTokens;
import static dev.ai4j.utils.TestUtils.messageFromSystemWithTokens;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

class ChatHistoryTest {

    @Test
    void should_keep_specified_number_of_tokens_in_chat_history_1() {

        val messageFromSystem = messageFromSystemWithTokens(10);
        val chatHistory = ChatHistory.builder()
                .messageFromSystem(messageFromSystem)
                .capacityInTokens(30)
                .build();
        assertThat(chatHistory.getMessageHistory())
                .hasSize(1)
                .containsExactly(messageFromSystem);

        val firstMessageFromHuman = messageFromHumanWithTokens(10);
        chatHistory.add(firstMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem,
                        firstMessageFromHuman
                );

        val firstMessageFromAi = messageFromAiWithTokens(10);
        chatHistory.add(firstMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(3)
                .containsExactly(
                        messageFromSystem,
                        firstMessageFromHuman,
                        firstMessageFromAi
                );

        val secondMessageFromHuman = messageFromHumanWithTokens(10);
        chatHistory.add(secondMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(3)
                .containsExactly(
                        messageFromSystem,
                        // firstMessageFromHuman was removed
                        firstMessageFromAi,
                        secondMessageFromHuman
                );

        val secondMessageFromAi = messageFromAiWithTokens(10);
        chatHistory.add(secondMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(3)
                .containsExactly(
                        messageFromSystem,
                        // firstMessageFromAi was removed
                        secondMessageFromHuman,
                        secondMessageFromAi
                );
    }

    @Test
    void should_keep_specified_number_of_tokens_in_chat_history_2() {

        val messageFromSystem = messageFromSystemWithTokens(5);
        val chatHistory = ChatHistory.builder()
                .messageFromSystem(messageFromSystem)
                .capacityInTokens(20)
                .build();
        assertThat(chatHistory.getMessageHistory())
                .hasSize(1)
                .containsExactly(messageFromSystem);

        val firstMessageFromHuman = messageFromHumanWithTokens(10);
        chatHistory.add(firstMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem, // 5 tokens
                        firstMessageFromHuman // 10 tokens
                );

        val firstMessageFromAi = messageFromAiWithTokens(10);
        chatHistory.add(firstMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem, // 5 tokens
                        // firstMessageFromHuman was removed
                        firstMessageFromAi  // 10 tokens
                );

        val secondMessageFromHuman = messageFromAiWithTokens(5);
        chatHistory.add(secondMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(3)
                .containsExactly(
                        messageFromSystem, // 5 tokens
                        // firstMessageFromHuman was removed
                        firstMessageFromAi, // 10 tokens
                        secondMessageFromHuman // 5 tokens
                );
    }

    @Test
    void should_keep_200_tokens_in_chat_history_by_default() {

        val messageFromSystem = messageFromSystemWithTokens(10);
        val chatHistory = ChatHistory.builder()
                .messageFromSystem(messageFromSystem)
                // user did not configure maxTokensInHistory
                .build();
        assertThat(chatHistory.getMessageHistory())
                .hasSize(1)
                .containsExactly(messageFromSystem);

        for (int i = 0; i < 30; i++) {
            chatHistory.add(messageFromHumanWithTokens(10));
        }

        assertThat(chatHistory.getMessageHistory())
                .contains(messageFromSystem, atIndex(0))
                .hasSize(20); // 20 messages 10 tokens each = 200 tokens
    }

    @Test
    void should_keep_specified_number_of_tokens_in_history_without_message_from_system() {

        val chatHistory = ChatHistory.builder()
                // user did not configure messageFromSystem
                .capacityInTokens(20)
                .build();
        assertThat(chatHistory.getMessageHistory())
                .hasSize(0);

        val firstMessageFromHuman = messageFromHumanWithTokens(10);
        chatHistory.add(firstMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(1)
                .containsExactly(firstMessageFromHuman);

        val firstMessageFromAi = messageFromAiWithTokens(10);
        chatHistory.add(firstMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        firstMessageFromHuman,
                        firstMessageFromAi
                );

        val secondMessageFromHuman = messageFromHumanWithTokens(10);
        chatHistory.add(secondMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        // firstMessageFromHuman was removed
                        firstMessageFromAi,
                        secondMessageFromHuman
                );

        val secondMessageFromAi = messageFromAiWithTokens(10);
        chatHistory.add(secondMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        // firstMessageFromAi was removed
                        secondMessageFromHuman,
                        secondMessageFromAi
                );
    }

    @Test
    void should_keep_specified_number_of_messages_in_chat_history() {

        val messageFromSystem = messageFromSystem("does not matter how many tokens");
        val chatHistory = ChatHistory.builder()
                .messageFromSystem(messageFromSystem)
                .capacityInMessages(3)
                .build();
        assertThat(chatHistory.getMessageHistory())
                .hasSize(1)
                .containsExactly(messageFromSystem);

        val firstMessageFromHuman = messageFromHuman("does not matter how many tokens");
        chatHistory.add(firstMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem,
                        firstMessageFromHuman
                );

        val firstMessageFromAi = messageFromAi("does not matter how many tokens");
        chatHistory.add(firstMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(3)
                .containsExactly(
                        messageFromSystem,
                        firstMessageFromHuman,
                        firstMessageFromAi
                );

        val secondMessageFromHuman = messageFromHuman("does not matter how many tokens");
        chatHistory.add(secondMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(3)
                .containsExactly(
                        messageFromSystem,
                        // firstMessageFromHuman was removed
                        firstMessageFromAi,
                        secondMessageFromHuman
                );

        val secondMessageFromAi = messageFromAi("does not matter how many tokens");
        chatHistory.add(secondMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(3)
                .containsExactly(
                        messageFromSystem,
                        // firstMessageFromAi was removed
                        secondMessageFromHuman,
                        secondMessageFromAi
                );
    }

    @Test
    void should_keep_specified_number_of_messages_in_chat_history_without_message_from_system() {

        val chatHistory = ChatHistory.builder()
                .capacityInMessages(2)
                .build();
        assertThat(chatHistory.getMessageHistory())
                .hasSize(0);

        val firstMessageFromHuman = messageFromHuman("does not matter how many tokens");
        chatHistory.add(firstMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(1)
                .containsExactly(firstMessageFromHuman);

        val firstMessageFromAi = messageFromAi("does not matter how many tokens");
        chatHistory.add(firstMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        firstMessageFromHuman,
                        firstMessageFromAi
                );

        val secondMessageFromHuman = messageFromHuman("does not matter how many tokens");
        chatHistory.add(secondMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        // firstMessageFromHuman was removed
                        firstMessageFromAi,
                        secondMessageFromHuman
                );

        val secondMessageFromAi = messageFromAi("does not matter how many tokens");
        chatHistory.add(secondMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        // firstMessageFromAi was removed
                        secondMessageFromHuman,
                        secondMessageFromAi
                );
    }

    @Test
    void should_keep_specified_number_of_tokens_and_number_of_messages_in_chat_history_1() {

        // In this test we will be using messages with 10 tokens each.
        // We will configure maxTokensInHistory(20) and maxMessagesInHistory(3):
        // With maxMessagesInHistory(3) we will be able to fit 3 messages into history.
        // But due to maxTokensInHistory(20) it will keep only 2.

        val messageFromSystem = messageFromSystemWithTokens(10);
        val chatHistory = ChatHistory.builder()
                .messageFromSystem(messageFromSystem)
                .capacityInTokens(20)
                .capacityInMessages(3)
                .build();
        assertThat(chatHistory.getMessageHistory())
                .hasSize(1)
                .containsExactly(messageFromSystem);

        val firstMessageFromHuman = messageFromHumanWithTokens(10);
        chatHistory.add(firstMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem,
                        firstMessageFromHuman
                );

        val firstMessageFromAi = messageFromAiWithTokens(10);
        chatHistory.add(firstMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem,
                        // firstMessageFromHuman was removed
                        firstMessageFromAi
                );

        val secondMessageFromHuman = messageFromHumanWithTokens(10);
        chatHistory.add(secondMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem,
                        // firstMessageFromAi was removed
                        secondMessageFromHuman
                );
    }

    @Test
    void should_keep_specified_number_of_tokens_and_number_of_messages_in_chat_history_2() {

        // In this test we will be using messages with 10 tokens each.
        // We will configure maxMessagesInHistory(2) and maxTokensInHistory(30):
        // With maxTokensInHistory(30) we will be able to fit 3 messages into history.
        // But due to maxMessagesInHistory(2) it will keep only 2.

        val messageFromSystem = messageFromSystemWithTokens(10);
        val chatHistory = ChatHistory.builder()
                .messageFromSystem(messageFromSystem)
                .capacityInTokens(30)
                .capacityInMessages(2)
                .build();
        assertThat(chatHistory.getMessageHistory())
                .hasSize(1)
                .containsExactly(messageFromSystem);

        val firstMessageFromHuman = messageFromHumanWithTokens(10);
        chatHistory.add(firstMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem,
                        firstMessageFromHuman
                );

        val firstMessageFromAi = messageFromAiWithTokens(10);
        chatHistory.add(firstMessageFromAi);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem,
                        // firstMessageFromHuman was removed
                        firstMessageFromAi
                );

        val secondMessageFromHuman = messageFromHumanWithTokens(10);
        chatHistory.add(secondMessageFromHuman);
        assertThat(chatHistory.getMessageHistory())
                .hasSize(2)
                .containsExactly(
                        messageFromSystem,
                        // firstMessageFromAi was removed
                        secondMessageFromHuman
                );
    }

    @Test
    void should_load_previous_messages_with_token_restriction() {

        val previousMessages = asList(
                messageFromHumanWithTokens(10),
                messageFromAiWithTokens(10),
                messageFromHumanWithTokens(10),
                messageFromAiWithTokens(10)
        );

        val chatHistory = ChatHistory.builder()
                .previousMessages(previousMessages)
                .capacityInTokens(30)
                .build();

        assertThat(chatHistory.getMessageHistory())
                .hasSize(3);
    }

    @Test
    void should_load_previous_messages_with_message_restriction() {

        val previousMessages = asList(
                messageFromHumanWithTokens(10),
                messageFromAiWithTokens(10),
                messageFromHumanWithTokens(10),
                messageFromAiWithTokens(10)
        );

        val chatHistory = ChatHistory.builder()
                .previousMessages(previousMessages)
                .capacityInMessages(3)
                .build();

        assertThat(chatHistory.getMessageHistory())
                .hasSize(3);
    }

    @Test
    void should_keep_all_history_without_restrictions() {

        val chatHistory = ChatHistory.builder()
                .removeCapacityRestrictionInTokens()
                .build();

        for (int i = 0; i < 1000; i++) {
            chatHistory.add(messageFromHumanWithTokens(1000));
        }

        assertThat(chatHistory.getMessageHistory())
                .hasSize(1000);
    }
}