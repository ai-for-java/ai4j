package dev.ai4j.model.embedding.openai;

import dev.ai4j.model.embedding.Embedding;
import dev.ai4j.model.embedding.EmbeddingModel;
import dev.ai4j.model.openai.OpenAiModel;
import dev.ai4j.schema.document.Document;
import dev.ai4j.utils.Utils;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.Builder;
import lombok.val;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import static dev.ai4j.model.openai.OpenAiModel.TEXT_EMBEDDING_ADA_002;
import static java.util.stream.Collectors.toList;

public class OpenAiEmbeddingModel implements EmbeddingModel {

    private final OpenAiService openAiService;
    private final OpenAiModel model;

    @Builder
    public OpenAiEmbeddingModel(String apiKey, OpenAiModel model) {
        openAiService = new OpenAiService(apiKey);
        this.model = model == null ? TEXT_EMBEDDING_ADA_002 : model;
    }

    @Override
    public Embedding embed(Document document) {
        return embed(Utils.list(document)).iterator().next();
    }

    @Override
    public Embedding embed(String text) {
        return embed(Utils.list(Document.from(text))).iterator().next();
    }

    @Override
    public Collection<Embedding> embed(Collection<Document> documents) {
        val documentContents = documents.stream()
                .map(Document::getContents)
                .collect(toList());

        val embeddingRequest = EmbeddingRequest.builder()
                .input(documentContents) // TODO handle newlines ?
                .model(model.getModelName())
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
