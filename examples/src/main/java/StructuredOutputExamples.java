import dev.ai4j.model.completion.OpenAiCompletionModel;
import dev.ai4j.model.completion.structured.Description;
import dev.ai4j.model.completion.structured.Example;

import java.util.List;

public class StructuredOutputExamples {

    static class GetOneStructuredOutput {

        @Description("a recipe of a tasty dish")
        static class Recipe {

            @Description("short description of a recipe in one sentence")
            String recipeDescription;

            @Description("recipe process step by step")
            List<String> steps;

            @Description("how many minutes does it take to prepare it")
            Integer durationInMinutes;
        }

        public static void main(String[] args) {

            OpenAiCompletionModel openAiChatModel = OpenAiCompletionModel.builder()
                    .apiKey(System.getenv("OPENAI_API_KEY")) // https://platform.openai.com/account/api-keys
                    .build();

            Recipe recipe = openAiChatModel.getOne(Recipe.class);

            print(recipe);
        }

        private static void print(Recipe recipe) {
            System.out.println();
            System.out.printf("Description: %s%n", recipe.recipeDescription);
            for (int i = 0; i < recipe.steps.size(); i++) {
                System.out.printf("Step %s: %s%n", i + 1, recipe.steps.get(i));
            }
            System.out.printf("Duration: %s minutes%n", recipe.durationInMinutes);
        }
    }

    public static class GetMultipleStructuredOutputs {

        @Description("a person")
        static class Person {

            @Description("first name of a person. should start with J")
            @Example("John")
            String firstName;

            @Description("last name of a person. should start with D")
            @Example("Doe")
            String lastName;

            @Description("age of a person. should be between 18 and 65")
            @Example("35")
            Integer age;
        }

        public static void main(String[] args) {

            OpenAiCompletionModel openAiChatModel = OpenAiCompletionModel.builder()
                    .apiKey(System.getenv("OPENAI_API_KEY")) // https://platform.openai.com/account/api-keys
                    .build();

            List<Person> persons = openAiChatModel.getMultiple(Person.class, 5);

            persons.forEach(GetMultipleStructuredOutputs::print);
        }

        private static void print(Person person) {
            System.out.println();
            System.out.printf("First name: %s%n", person.firstName);
            System.out.printf("Last name: %s%n", person.lastName);
            System.out.printf("Age: %s years%n", person.age);
        }
    }
}
