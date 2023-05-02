package dev.ai4j.model.language.prompt;

import lombok.var;

import java.util.Map;

public class PromptTemplate {

    private final String template;

    public PromptTemplate(String template) {
        this.template = template;
    }

    public Prompt apply(Map<String, Object> params) {
        var prompt = template;

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            prompt = prompt.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }

        return Prompt.from(prompt);
    }

    public static PromptTemplate from(String template) {
        return new PromptTemplate(template);
    }
}
