package dev.ai4j.prompt;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PromptTest {

    @Test
    void should_create_prompt() {
        val promptText = "You are a helpful assistant.";

        val prompt = new Prompt(promptText);

        assertThat(prompt.getPromptText()).isEqualTo(promptText);
    }

    @Test
    void should_throw_if_prompt_text_contains_unresolved_parameter() {
        val promptText = "My name is {{name}}.";

        assertThatThrownBy(() -> new Prompt(promptText))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Found unresolved parameter(s) [name] in prompt 'My name is {{name}}.'");
    }

    @Test
    void should_throw_if_prompt_text_contains_multiple_unresolved_parameters() {
        val promptText = "My name is {{first_name}} {{second_name}}.";

        assertThatThrownBy(() -> new Prompt(promptText))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Found unresolved parameter(s) [first_name, second_name] in prompt 'My name is {{first_name}} {{second_name}}.'");
    }
}
