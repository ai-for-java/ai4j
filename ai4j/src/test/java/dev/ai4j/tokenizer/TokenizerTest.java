package dev.ai4j.tokenizer;

import lombok.val;
import org.junit.jupiter.api.Test;

import static dev.ai4j.utils.TestUtils.repeat;
import static org.assertj.core.api.Assertions.assertThat;

class TokenizerTest {

    Tokenizer tokenizer = new Tokenizer();

    @Test
    void should_encode_and_decode_text() {
        val originalText = "This is a text which will be encoded and decoded back.";

        val tokens = tokenizer.encode(originalText);
        val decodedText = tokenizer.decode(tokens);

        assertThat(decodedText).isEqualTo(originalText);
    }

    @Test
    void should_encode_with_truncation_and_decode_text() {
        val originalText = "This is a text which will be encoded with truncation and decoded back.";

        val tokens = tokenizer.encode(originalText, 10);
        assertThat(tokens).hasSize(10);

        val decodedText = tokenizer.decode(tokens);
        assertThat(decodedText).isEqualTo("This is a text which will be encoded with trunc");
    }

    @Test
    void should_count_tokens_in_short_texts() {
        assertThat(tokenizer.countTokens("Hello")).isEqualTo(1);
        assertThat(tokenizer.countTokens("Hello!")).isEqualTo(2);
        assertThat(tokenizer.countTokens("Hello, how are you?")).isEqualTo(6);
    }

    @Test
    void should_count_tokens_in_average_text() {
        val text1 = "Hello, how are you doing? What do you want to talk about?";
        assertThat(tokenizer.countTokens(text1)).isEqualTo(15);

        val text2 = String.join(" ", repeat("Hello, how are you doing? What do you want to talk about?", 2));
        assertThat(tokenizer.countTokens(text2)).isEqualTo(2 * 15);

        val text3 = String.join(" ", repeat("Hello, how are you doing? What do you want to talk about?", 3));
        assertThat(tokenizer.countTokens(text3)).isEqualTo(3 * 15);
    }

    @Test
    void should_count_tokens_in_large_text() {
        val text1 = String.join(" ", repeat("Hello, how are you doing? What do you want to talk about?", 10));
        assertThat(tokenizer.countTokens(text1)).isEqualTo(10 * 15);

        val text2 = String.join(" ", repeat("Hello, how are you doing? What do you want to talk about?", 50));
        assertThat(tokenizer.countTokens(text2)).isEqualTo(50 * 15);

        val text3 = String.join(" ", repeat("Hello, how are you doing? What do you want to talk about?", 100));
        assertThat(tokenizer.countTokens(text3)).isEqualTo(100 * 15);
    }
}