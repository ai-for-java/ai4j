package dev.ai4j.prompt;

import lombok.val;
import lombok.var;

import java.util.Map;

public class PromptTemplate {

    private final String template;

    public PromptTemplate(String template) {
        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("Template cannot be null or empty");
        }
        this.template = template;
    }

    public Prompt apply(Map<String, Object> parameters) {
        var prompt = template;

        if (parameters == null || parameters.isEmpty()) {
            throw new IllegalArgumentException("Parameters cannot be null or empty");
        }

        for (val entry : parameters.entrySet()) {
            prompt = replaceAll(prompt, entry.getKey(), entry.getValue().toString());
        }

        return Prompt.from(prompt);
    }

    public Prompt apply(String parameterName, Object parameterValue) {
        return Prompt.from(replaceAll(template, parameterName, parameterValue));
    }

    public Prompt.Builder buildPrompt() {
        return Prompt.buildFrom(this);
    }

    private static String replaceAll(String template, String parameterName, Object parameterValue) {
        validate(parameterName);
        validate(parameterValue);
        verifyParameterExists(parameterName, template);
        return template.replaceAll(inDoubleCurlyBracketsEscaped(parameterName), parameterValue.toString());
    }

    private static void validate(String parameterName) {
        if (parameterName == null || parameterName.isEmpty()) {
            throw new IllegalArgumentException("Parameter name cannot be null");
        }
    }

    private static void validate(Object parameterValue) {
        if (parameterValue == null
                || parameterValue.toString() == null
                || parameterValue.toString().isEmpty()) {
            throw new IllegalArgumentException("Parameter value cannot be null");
        }
    }

    private static void verifyParameterExists(String parameterName, String template) {
        if (!template.contains(inDoubleCurlyBrackets(parameterName))) {
            throw new IllegalArgumentException(String.format("There is no parameter '%s' in prompt template '%s'", parameterName, template));
        }
    }

    private static String inDoubleCurlyBrackets(String parameterName) {
        return "{{" + parameterName + "}}";
    }

    private static String inDoubleCurlyBracketsEscaped(String parameterName) {
        return "\\{\\{" + parameterName + "\\}\\}";
    }

    public static PromptTemplate from(String template) {
        return new PromptTemplate(template);
    }
}
