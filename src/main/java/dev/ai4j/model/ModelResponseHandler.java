package dev.ai4j.model;

public interface ModelResponseHandler {

    default void handleResponseFragment(String responseFragment) {
    }

    default void handleCompleteResponse(String completeResponse) {
    }

    default void handleError(Throwable t) {
    }
}
