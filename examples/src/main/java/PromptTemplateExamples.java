import dev.ai4j.prompt.Prompt;
import dev.ai4j.prompt.PromptTemplate;

import java.util.Map;

public class PromptTemplateExamples {

    static class PromptTemplateWithBuilder {

        public static void main(String[] args) {

            Prompt prompt = Prompt.from("Hi, my name is {{name}}. I am {{age}} years old.")
                    .with("name", "John")
                    .with("age", 35)
                    .build();

            System.out.println(prompt);
        }
    }

    static class PromptTemplateWithOneParameter {

        public static void main(String[] args) {

            PromptTemplate promptTemplate = PromptTemplate.from("Hi, my name is {{name}}.");

            Prompt prompt = promptTemplate.with("name", "John");

            System.out.println(prompt);
        }
    }

    static class PromptTemplateWithMultipleParameters {

        public static void main(String[] args) {

            PromptTemplate promptTemplate = PromptTemplate.from("Hi, my name is {{name}}. I am {{age}} years old.");

            Prompt prompt = promptTemplate.with(Map.of(
                    "name", "John",
                    "age", 35
            ));

            System.out.println(prompt);
        }
    }
}
