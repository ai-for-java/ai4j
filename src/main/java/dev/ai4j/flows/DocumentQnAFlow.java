package dev.ai4j.flows;

import dev.ai4j.PromptTemplate;
import dev.ai4j.chat.ChatModel;
import dev.ai4j.document.Document;
import dev.ai4j.document.DocumentLoader;
import dev.ai4j.document.DocumentSplitter;
import dev.ai4j.document.splitter.OverlappingDocumentSplitter;
import dev.ai4j.embedding.Embedding;
import dev.ai4j.embedding.EmbeddingModel;
import dev.ai4j.embedding.VectorDatabase;
import lombok.Builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class DocumentQnAFlow {

    private static final OverlappingDocumentSplitter DEFAULT_DOCUMENT_SPLITTER
            = new OverlappingDocumentSplitter(1000, 200);
    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("Using the information delimited by triple angle brackets, answer the following question to the best of your ability: {{question}} <<<{{information}}>>>");

    private final DocumentLoader documentLoader;
    private final DocumentSplitter documentSplitter;
    private final EmbeddingModel embeddingModel;
    private final VectorDatabase vectorDatabase;
    private final PromptTemplate promptTemplate;
    private final ChatModel chatModel;

    @Builder
    public DocumentQnAFlow(DocumentLoader documentLoader,
                           DocumentSplitter documentSplitter,
                           EmbeddingModel embeddingModel, // TODO provide possibility to use same openapi key
                           VectorDatabase vectorDatabase,
                           PromptTemplate promptTemplate,
                           ChatModel chatModel) {
        this.documentLoader = documentLoader;
        this.documentSplitter = documentSplitter == null ? DEFAULT_DOCUMENT_SPLITTER : documentSplitter;
        this.embeddingModel = embeddingModel;
        this.vectorDatabase = vectorDatabase;
        this.promptTemplate = promptTemplate == null ? DEFAULT_PROMPT_TEMPLATE : promptTemplate;
        this.chatModel = chatModel;

        init();
    }

    private void init() {
        Document document = documentLoader.load();
        List<Document> chunks = documentSplitter.split(document);
        Collection<Embedding> embeddings = embeddingModel.embed(chunks);
        vectorDatabase.persist(embeddings);
    }

    public String ask(String question) {
        Embedding questionEmbedding = embeddingModel.embed(question);

        List<Embedding> relatedEmbeddings = vectorDatabase.findRelated(questionEmbedding, 5); // TODO defaults

        String concatenatedEmbeddings = relatedEmbeddings.stream()
                .map(Embedding::contents)
                .collect(joining(" "));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("question", question);
        parameters.put("information", concatenatedEmbeddings);

        String prompt = promptTemplate.format(parameters);

        return chatModel.chat(prompt);
    }
}
