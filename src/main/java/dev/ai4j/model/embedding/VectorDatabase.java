package dev.ai4j.model.embedding;

import dev.ai4j.model.embedding.Embedding;

import java.util.Collection;

public interface VectorDatabase {

    void persist(Embedding embedding);

    void persist(Collection<Embedding> embeddings);

    Collection<Embedding> findRelated(Embedding embedding);

    Collection<Embedding> findRelated(Embedding embedding, int maxResults);
}
