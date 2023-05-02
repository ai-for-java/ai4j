package dev.ai4j.model.embedding;

import lombok.Value;

import java.util.List;

@Value
public class Embedding {

    String text;
    List<Double> vector;
}