package dev.ai4j.document.splitter;

import dev.ai4j.document.Document;

import java.util.List;

public interface DocumentSplitter {

    List<Document> split(Document document);
}
