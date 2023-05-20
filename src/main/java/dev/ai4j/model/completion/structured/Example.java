package dev.ai4j.model.completion.structured;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface Example {

    String[] value();
}
