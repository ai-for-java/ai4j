package dev.ai4j.model.embedding;

import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import dev.ai4j.document.Document;
import dev.ai4j.model.embedding.Embedding;
import dev.ai4j.model.embedding.EmbeddingModel;
import dev.ai4j.model.openai.OpenAiModelName;
import lombok.Builder;
import lombok.val;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static dev.ai4j.model.openai.OpenAiModelName.TEXT_EMBEDDING_ADA_002;
import static dev.ai4j.utils.Utils.list;
import static java.util.stream.Collectors.toList;

public class OpenAiEmbeddingModel implements EmbeddingModel {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);

    private final OpenAiService openAiService;
    private final OpenAiModelName modelName;

    @Builder
    public OpenAiEmbeddingModel(String apiKey, OpenAiModelName modelName, Duration timeout) {
        this.openAiService = new OpenAiService(apiKey, timeout == null ? DEFAULT_TIMEOUT : timeout);
        this.modelName = modelName == null ? TEXT_EMBEDDING_ADA_002 : modelName;
    }

    @Override
    public Embedding embed(Document document) {
        return embed(list(document)).iterator().next();
    }

    @Override
    public Embedding embed(String text) {
        return embed(list(Document.from(text))).iterator().next();
    }

    @Override
    public Collection<Embedding> embed(Collection<Document> documents) {
        val documentContents = documents.stream()
                .map(Document::getContents)
                .collect(toList());

        val embeddingRequest = EmbeddingRequest.builder()
                .input(documentContents) // TODO handle newlines ?
                .model(modelName.getId())
                .build();

        val openAiEmbeddings = openAiService.createEmbeddings(embeddingRequest).getData();

        return zip(documentContents, openAiEmbeddings);
    }

    private static List<Embedding> zip(List<String> documentTexts, List<com.theokanning.openai.embedding.Embedding> openAiEmbeddings) {
        return IntStream.range(0, documentTexts.size())
                .mapToObj(i -> new Embedding(documentTexts.get(i), openAiEmbeddings.get(i).getEmbedding()))
                .collect(toList());
    }
}
