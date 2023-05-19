package dev.ai4j.prompt;

import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PromptTemplateTest {

    @Test
    void should_create_prompt_from_template_with_one_parameter() {
        val promptTemplate = new PromptTemplate("My name is {{name}}.");

        val prompt = promptTemplate.apply("name", "Klaus");

        assertThat(prompt.getPromptText()).isEqualTo("My name is Klaus.");
    }

    @Test
    void should_create_prompt_from_template_with_multiple_parameters() {
        val promptTemplate = new PromptTemplate("My name is {{name}} {{surname}}.");

        val prompt = promptTemplate.apply(ImmutableMap.of(
                "name", "Klaus",
                "surname", "Heißler"
        ));

        assertThat(prompt.getPromptText()).isEqualTo("My name is Klaus Heißler.");
    }

    @Test
    void should_throw_when_parameter_is_unknown() {
        val promptTemplate = new PromptTemplate("My name is {{name}}.");

        assertThatThrownBy(() -> promptTemplate.apply("banana", "banana"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("There is no parameter 'banana' in prompt template 'My name is {{name}}.'");
    }

    // TODO bad template, bad parameters, bad values

//    @Test
//    void should_create_prompt_from_template_without_parameters() { // TODO needed?
//        val promptTemplate = new PromptTemplate("My name is Klaus.");
//
//        val prompt = promptTemplate.apply(new HashMap<>());
//
//        assertThat(prompt.getPromptText()).isEqualTo("My name is Klaus.");
//    }
}