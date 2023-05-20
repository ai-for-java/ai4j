import com.google.common.collect.ImmutableMap;
import dev.ai4j.document.Document;
import dev.ai4j.document.loader.PdfFileLoader;
import dev.ai4j.document.splitter.DocumentSplitter;
import dev.ai4j.document.splitter.OverlappingDocumentSplitter;
import dev.ai4j.model.completion.OpenAiCompletionModel;
import dev.ai4j.model.embedding.Embedding;
import dev.ai4j.model.embedding.OpenAiEmbeddingModel;
import dev.ai4j.model.embedding.PineconeDatabase;
import dev.ai4j.prompt.Prompt;
import dev.ai4j.prompt.PromptTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static dev.ai4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static dev.ai4j.model.openai.OpenAiModelName.TEXT_EMBEDDING_ADA_002;
import static java.util.stream.Collectors.joining;

public class PdfFileOpenAiPineconeExample {

    // TODO FLow

    public static void main(String[] args) {

        // Load file with information you want to "chat" with LLM about.
        // Currently, text and PDF files are supported.

        String absolutePathToPdfFile = System.getProperty("user.dir") + "/examples/src/main/java/large-language-models.pdf";
        PdfFileLoader pdfFileLoader = new PdfFileLoader(absolutePathToPdfFile);
        Document fullPdfDocument = pdfFileLoader.load();


        // Split the file into small chunks of 200 characters each with an overlap of 40 characters

        DocumentSplitter splitter = new OverlappingDocumentSplitter(200, 40);
        List<Document> pdfDocumentChunks = splitter.split(fullPdfDocument);


        // Convert chunks into embeddings (semantic vectors)

        OpenAiEmbeddingModel openAiEmbeddings = OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY")) // https://platform.openai.com/account/api-keys
                .modelName(TEXT_EMBEDDING_ADA_002)
                .build();
        Collection<Embedding> embeddings = openAiEmbeddings.embed(pdfDocumentChunks);


        // Store embeddings into vector DB for further search / retrieval

        PineconeDatabase pineconeDatabase = PineconeDatabase.builder()
                .apiKey(System.getenv("PINECONE_API_KEY")) // https://app.pinecone.io/organizations/xxx/projects/yyy:zzz/keys
                .environment("northamerica-northeast1-gcp")
                .projectName("19a129b")
                .index("test-s1-1536") // make sure the dimensions of the Pinecone index match the dimensions of the embedding model (1536 for text-embedding-ada-002)
                .build();
        pineconeDatabase.persist(embeddings);


        // Define the question you want to ask LLM

        String question = "How many parameters does GPT-3 have?";


        // Find relevant embeddings in vector DB by semantic similarity

        Embedding embeddingForQuestion = openAiEmbeddings.embed(question);
        Collection<Embedding> relatedEmbeddings = pineconeDatabase.findRelated(embeddingForQuestion);


        // Create a prompt for LLM that includes original question and relevant embeddings

        Prompt prompt = Prompt.from("""
                Using only the information enclosed in triple angle brackets, answer this question: {{question}} <<<{{embeddings}}>>>
                """)
                .with("question", question)
                .with("embeddings", relatedEmbeddings.stream().map(Embedding::getContent).collect(joining("\n\n")))
                .build();


        // Send prompt to LLM

        OpenAiCompletionModel openAiLanguageModel = OpenAiCompletionModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY")) // https://platform.openai.com/account/api-keys
                .modelName(GPT_3_5_TURBO)
                .build();
        String answer = openAiLanguageModel.complete(prompt);


        // See an answer from LLM

        System.out.println(answer);
    }
}
