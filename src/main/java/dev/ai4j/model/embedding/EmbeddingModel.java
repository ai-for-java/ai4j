package dev.ai4j.model.embedding;

import dev.ai4j.schema.document.Document;

import java.util.Collection;

public interface EmbeddingModel {

    Embedding embed(Document document);

    Embedding embed(String text);

    Collection<Embedding> embed(Collection<Document> documents);
}