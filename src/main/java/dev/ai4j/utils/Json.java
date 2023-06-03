package dev.ai4j.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.ai4j.model.completion.structured.Description;
import dev.ai4j.model.completion.structured.Example;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

public class Json {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static String toJson(Object o) {
        return GSON.toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    public static <S> String generateJsonStructure(Class<S> structured) {
        StringBuilder jsonStructure = new StringBuilder();

        jsonStructure.append("{\n");
        for (Field field : structured.getDeclaredFields()) {
            Description fieldDescription = field.getAnnotation(Description.class);
            if (fieldDescription == null) {
                throw new RuntimeException(String.format("Field %s is not annotated with @Description(\"...\")", field.getName()));
            }
            jsonStructure.append(String.format("\"%s\": // %s,\n", field.getName(), fieldDescription.value()));
        }
        jsonStructure.deleteCharAt(jsonStructure.length() - 2);
        jsonStructure.append("}");

        return jsonStructure.toString();
    }

    public static <S> Optional<String> generateJsonExample(Class<S> structured) {
        if (!hasExamples(structured)) {
            return Optional.empty();
        }

        StringBuilder jsonExample = new StringBuilder();

        jsonExample.append("{\n");
        for (Field field : structured.getDeclaredFields()) {
            Example fieldExample = field.getAnnotation(Example.class);
            if (fieldExample == null) {
                throw new RuntimeException(String.format("Field %s is not annotated with @Example(\"...\")", field.getName()));
            }
            jsonExample.append(String.format("\"%s\": %s,\n", field.getName(), toJsonExample(field)));
        }
        jsonExample.deleteCharAt(jsonExample.length() - 2);
        jsonExample.append("}");

        return Optional.of(jsonExample.toString());
    }

    private static <S> boolean hasExamples(Class<S> structured) {
        return stream(structured.getDeclaredFields())
                .anyMatch(field -> field.isAnnotationPresent(Example.class));
    }

    public static String toJsonExample(Field field) {
        Example fieldExample = field.getAnnotation(Example.class);
        String[] examples = fieldExample.value();

        Class<?> fieldType = field.getType();
        boolean wrapInQuotes = fieldType == String.class
                || fieldType == String[].class
                || isCollectionOfStrings(field);

        if (examples.length == 1) {
            if (wrapInQuotes) {
                return "\"" + examples[0] + "\"";
            }
            return examples[0];
        }

        return String.format("[%s]", stream(examples).map(example -> {
            if (wrapInQuotes) {
                return "\"" + example + "\"";
            }

            return example;
        }).collect(joining(", ")));
    }

    private static boolean isCollectionOfStrings(Field field) {
        if (!Collection.class.isAssignableFrom(field.getType())) {
            return false;
        }

        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        Class<?> actualTypeArgument = (Class<?>) genericType.getActualTypeArguments()[0];

        return actualTypeArgument.equals(String.class);
    }
}
