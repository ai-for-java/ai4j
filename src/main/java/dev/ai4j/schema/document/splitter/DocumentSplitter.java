package dev.ai4j.schema.document.splitter;

import dev.ai4j.schema.document.Document;

import java.util.List;

public interface DocumentSplitter {

    List<Document> split(Document document);
}
