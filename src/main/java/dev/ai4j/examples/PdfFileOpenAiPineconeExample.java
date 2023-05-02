package dev.ai4j.examples;

import dev.ai4j.model.embedding.Embedding;
import dev.ai4j.model.embedding.openai.OpenAiEmbeddingModel;
import dev.ai4j.model.language.openai.OpenAiLanguageModel;
import dev.ai4j.model.language.prompt.PromptTemplate;
import dev.ai4j.schema.document.Document;
import dev.ai4j.schema.document.loader.PdfFileLoader;
import dev.ai4j.schema.document.splitter.DocumentSplitter;
import dev.ai4j.schema.document.splitter.OverlappingDocumentSplitter;
import dev.ai4j.vector.pinecone.PineconeDatabase;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static dev.ai4j.model.openai.OpenAiModel.GPT_3_5_TURBO;
import static dev.ai4j.model.openai.OpenAiModel.TEXT_EMBEDDING_ADA_002;
import static java.util.stream.Collectors.joining;

public class PdfFileOpenAiPineconeExample {

    public static void main(String[] args) {

        // load PDF file with information you want to "talk" to LLM about

        String absolutePathToPdfFile = System.getProperty("user.dir") + "/src/main/java/dev/ai4j/examples/large-language-models.pdf";
        PdfFileLoader pdfFileLoader = new PdfFileLoader(absolutePathToPdfFile);
        Document fullPdfDocument = pdfFileLoader.load();


        // split PDF file into small chunks 200 characters each and 40 character overlap

        DocumentSplitter splitter = new OverlappingDocumentSplitter(200, 40);
        List<Document> pdfDocumentChunks = splitter.split(fullPdfDocument);


        // convert chunks into embeddings (semantic vectors) using LLM

        OpenAiEmbeddingModel openAiEmbeddings = OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .model(TEXT_EMBEDDING_ADA_002)
                .build();
        Collection<Embedding> embeddings = openAiEmbeddings.embed(pdfDocumentChunks);


        // store embeddings into Pinecone (vector database) for further search / retrieval

        PineconeDatabase pineconeDatabase = PineconeDatabase.builder()
                .apiKey(System.getenv("PINECONE_API_KEY"))
                .environment("northamerica-northeast1-gcp")
                .projectName("19a129b")
                .index("test-s1-1536")
                .build();
        pineconeDatabase.persist(embeddings);


        // define a question you want to ask LLM

        String question = "How many parameters does GPT-3 have?";

        // find related embeddings in Pinecone (by semantic similarity)

        Embedding embeddingForQuestion = openAiEmbeddings.embed(question);
        Collection<Embedding> relatedEmbeddings = pineconeDatabase.findRelated(embeddingForQuestion);


        // create a prompt for LLM that includes original question and found embeddings

        PromptTemplate promptTemplate = PromptTemplate.from("Using only the information enclosed in triple angle brackets, answer this question: {question} <<<{embeddings}>>>");
        Map<String, Object> parameters = ImmutableMap.of(
                "question", question,
                "embeddings", relatedEmbeddings.stream()
                        .map(Embedding::getText)
                        .collect(joining("\n\n"))
        );

        // send prompt to LLM

        OpenAiLanguageModel openAiLanguageModel = OpenAiLanguageModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .model(GPT_3_5_TURBO)
                .build();
        String answer = openAiLanguageModel.complete(promptTemplate.apply(parameters));

        // see answer from LLM

        System.out.println(answer);
    }
}
