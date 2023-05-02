package dev.ai4j.utils;

import java.util.Arrays;
import java.util.List;

public class Utils {

    public static <T> List<T> list(T... elements) {
        return Arrays.asList(elements);
    }
}
