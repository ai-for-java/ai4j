package dev.ai4j.agent.tool;

import java.util.Optional;

public interface Tool {

    String id();

    String description();

    default boolean canHandle(String action) {
        return id().equalsIgnoreCase(action);
    }

    Optional<String> execute(String input);
}
