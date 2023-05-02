package dev.ai4j.model.language.prompt;

import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class PromptTemplateTest {

    @Test
    void testTemplateWithoutPlaceholders() {
        val promptTemplate = new PromptTemplate("Template without placeholders.");

        val prompt = promptTemplate.apply(new HashMap<>());

        assertThat(prompt.getText()).isEqualTo("Template without placeholders.");
    }

    @Test
    void testTemplateWithOnePlaceholder() {
        val promptTemplate = new PromptTemplate("Template with {one} placeholder.");

        val prompt = promptTemplate.apply(ImmutableMap.of("one", "this"));

        assertThat(prompt.getText()).isEqualTo("Template with this placeholder.");
    }

    @Test
    void testTemplateWithMultiplePlaceholder() {
        val promptTemplate = new PromptTemplate("Template with {first} and {second} placeholders.");

        val prompt = promptTemplate.apply(ImmutableMap.of(
                "first", 1,
                "second", 2
        ));

        assertThat(prompt.getText()).isEqualTo("Template with 1 and 2 placeholders.");
    }
}