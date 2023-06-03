package dev.ai4j.model;

public interface ModelResponseHandler {

    default void handleResponseFragment(String responseFragment) {
    }

    default void handleResponseCompletion() {
    }

    default void handleError(Throwable t) {
    }
}
