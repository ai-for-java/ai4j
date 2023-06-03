package dev.ai4j.model.embedding;

import dev.ai4j.document.Document;
import dev.ai4j.model.openai.OpenAiModelName;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.embedding.EmbeddingRequest;
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

    private final OpenAiClient client;
    private final OpenAiModelName modelName;

    @Builder
    public OpenAiEmbeddingModel(String apiKey, OpenAiModelName modelName, Duration timeout) {
        this.client = OpenAiClient.builder()
                .apiKey(apiKey)
                .timeout(timeout == null ? DEFAULT_TIMEOUT : timeout)
                .build();
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

        val openAiEmbeddings = client.embedding(embeddingRequest).execute().data();

        return zip(documentContents, openAiEmbeddings);
    }

    private static List<Embedding> zip(List<String> documentTexts, List<dev.ai4j.openai4j.embedding.Embedding> openAiEmbeddings) {
        return IntStream.range(0, documentTexts.size())
                .mapToObj(i -> new Embedding(documentTexts.get(i), toDoubles(openAiEmbeddings.get(i).embedding())))
                .collect(toList());
    }

    private static List<Double> toDoubles(List<Float> floats) {
        return floats.stream()
                .map(Float::doubleValue)
                .collect(toList());
    }
}
