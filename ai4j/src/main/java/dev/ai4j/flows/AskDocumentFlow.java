package dev.ai4j.flows;

import dev.ai4j.document.loader.DocumentLoader;
import dev.ai4j.document.splitter.DocumentSplitter;
import dev.ai4j.document.splitter.OverlappingDocumentSplitter;
import dev.ai4j.model.chat.ChatHistory;
import dev.ai4j.model.chat.ChatModel;
import dev.ai4j.model.embedding.Embedding;
import dev.ai4j.model.embedding.EmbeddingModel;
import dev.ai4j.model.embedding.VectorDatabase;
import dev.ai4j.prompt.PromptTemplate;
import lombok.Builder;
import lombok.val;

import static java.util.stream.Collectors.joining;

public class AskDocumentFlow {

    private static final OverlappingDocumentSplitter DEFAULT_DOCUMENT_SPLITTER
            = new OverlappingDocumentSplitter(1000, 200);
    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate("Using the information delimited by triple angle brackets, answer the question to the best of your ability: {{question}} <<<{{information}}>>>");

    private final DocumentLoader documentLoader;
    private final DocumentSplitter documentSplitter;
    private final EmbeddingModel embeddingModel;
    private final VectorDatabase vectorDatabase;
    private final PromptTemplate promptTemplate;
    private final ChatFlow chatFlow;
    // TODO prompt template?

    @Builder
    public AskDocumentFlow(DocumentLoader documentLoader,
                           DocumentSplitter documentSplitter,
                           EmbeddingModel embeddingModel, // TODO provide possibility to use same openapi key
                           VectorDatabase vectorDatabase,
                           PromptTemplate promptTemplate,
                           ChatModel chatModel,
                           ChatHistory chatHistory) {
        this.documentLoader = documentLoader;
        this.documentSplitter = documentSplitter == null ? DEFAULT_DOCUMENT_SPLITTER : documentSplitter;
        this.embeddingModel = embeddingModel;
        this.vectorDatabase = vectorDatabase;
        this.promptTemplate = promptTemplate == null ? DEFAULT_PROMPT_TEMPLATE : promptTemplate;
        this.chatFlow = ChatFlow.builder()
                .chatModel(chatModel)
                .chatHistory(chatHistory)
                .build();

        init();
    }

    private void init() {
        val document = documentLoader.load();
        val chunks = documentSplitter.split(document);
        val embeddings = embeddingModel.embed(chunks);
        vectorDatabase.persist(embeddings);
    }

    public String ask(String question) {
        val questionEmbedding = embeddingModel.embed(question);

        val relatedEmbeddings = vectorDatabase.findRelated(questionEmbedding);

        val concatenatedEmbeddings = relatedEmbeddings.stream()
                .map(Embedding::getContent)
                .collect(joining(" "));

        val prompt = promptTemplate.buildPrompt()
                .with("question", question)
                .with("information", concatenatedEmbeddings)
                .build();

        return chatFlow.humanSaid(prompt.getPrompt());
    }
}