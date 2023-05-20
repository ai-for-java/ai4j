package dev.ai4j.prompt;

import lombok.Value;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Value
public class Prompt {

    private static final Pattern PROMPT_TEMPLATE_PARAMETER_PATTERN = Pattern.compile("\\{\\{(.+?)}}");

    String prompt;

    public Prompt(String prompt) {
        verifyAllParametersResolved(prompt);
        this.prompt = prompt;
    }

    public static Prompt of(String prompt) {
        return new Prompt(prompt);
    }

    public static Builder from(PromptTemplate promptTemplate) {
        return new Builder(promptTemplate);
    }

    public static Builder from(String promptTemplate) {
        return new Builder(PromptTemplate.from(promptTemplate));
    }

    public static class Builder {

        private final PromptTemplate promptTemplate;
        private final Map<String, Object> parameters = new HashMap<>();

        public Builder(PromptTemplate promptTemplate) {
            this.promptTemplate = promptTemplate;
        }

        public Builder with(String key, Object value) {
            parameters.put(key, value);
            return this;
        }

        public Prompt build() {
            return promptTemplate.with(parameters);
        }
    }

    private static void verifyAllParametersResolved(String prompt) {
        val unresolvedParameterNames = getUnresolvedParameterNames(prompt);
        if (!unresolvedParameterNames.isEmpty()) {
            throw new IllegalArgumentException(String.format("Found unresolved parameter(s) [%s] in prompt '%s'",
                    String.join(", ", unresolvedParameterNames),
                    prompt
            ));
        }
    }

    private static List<String> getUnresolvedParameterNames(String prompt) {
        val unresolvedParameterNames = new ArrayList<String>();
        val matcher = PROMPT_TEMPLATE_PARAMETER_PATTERN.matcher(prompt);

        while (matcher.find()) {
            unresolvedParameterNames.add(matcher.group(1));
        }

        return unresolvedParameterNames;
    }

    @Override
    public String toString() {
        return prompt;
    }
}
