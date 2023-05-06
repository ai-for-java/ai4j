package dev.ai4j.model.embedding;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import io.pinecone.PineconeClient;
import io.pinecone.PineconeClientConfig;
import io.pinecone.PineconeConnection;
import io.pinecone.PineconeConnectionConfig;
import io.pinecone.proto.FetchRequest;
import io.pinecone.proto.QueryRequest;
import io.pinecone.proto.QueryVector;
import io.pinecone.proto.ScoredVector;
import io.pinecone.proto.UpsertRequest;
import io.pinecone.proto.Vector;
import lombok.Builder;
import lombok.val;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static dev.ai4j.utils.Utils.list;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class PineconeDatabase implements VectorDatabase {

    private static final String DEFAULT_NAMESPACE = "default";
    private static final String METADATA_ORIGINAL_TEXT = "text";

    PineconeConnection connection;
    String nameSpace;

    @Builder
    public PineconeDatabase(String apiKey, String environment, String projectName, String index, String nameSpace) {

        val configuration = new PineconeClientConfig()
                .withApiKey(apiKey)
                .withEnvironment(environment)
                .withProjectName(projectName);

        val pineconeClient = new PineconeClient(configuration);

        val connectionConfig = new PineconeConnectionConfig()
                .withIndexName(index);

        this.connection = pineconeClient.connect(connectionConfig); // TODO close
        this.nameSpace = nameSpace == null ? DEFAULT_NAMESPACE : nameSpace;
    }

    @Override
    public void persist(Embedding embedding) {
        persist(list(embedding));
    }

    @Override
    public void persist(Collection<Embedding> embeddings) {

        val upsertRequestBuilder = UpsertRequest.newBuilder()
                .setNamespace(nameSpace);

        embeddings.forEach(embedding -> {

            val originalText = Value.newBuilder()
                    .setStringValue(embedding.getText())
                    .build();

            val vectorMetadata = Struct.newBuilder()
                    .putFields(METADATA_ORIGINAL_TEXT, originalText)
                    .build();

            val vector = Vector.newBuilder()
                    .setId(createUniqueId())
                    .addAllValues(toFloats(embedding))
                    .setMetadata(vectorMetadata)
                    .build();

            upsertRequestBuilder.addVectors(vector);
        });

        connection.getBlockingStub().upsert(upsertRequestBuilder.build());

        // TODO verify that all embeddings are persisted ?
    }

    @Override
    public Collection<Embedding> findRelated(Embedding embedding) {
        return findRelated(embedding, 4);
    }

    @Override
    public Collection<Embedding> findRelated(Embedding embedding, int maxResults) {

        val queryVector = QueryVector
                .newBuilder()
                .addAllValues(toFloats(embedding))
                .setTopK(maxResults)
                .setNamespace(nameSpace)
                .build();

        val queryRequest = QueryRequest
                .newBuilder()
                .addQueries(queryVector)
                .setTopK(maxResults)
                .build();

        val matchedVectorIds = connection.getBlockingStub()
                .query(queryRequest)
                .getResultsList()
                .get(0) // TODO ?
                .getMatchesList()
                .stream()
                .map(ScoredVector::getId)
                .collect(toList());

        val matchedVectors = connection.getBlockingStub().fetch(FetchRequest.newBuilder()
                        .addAllIds(matchedVectorIds)
                        .setNamespace(nameSpace)
                        .build())
                .getVectorsMap()
                .values();

        return matchedVectors.stream()
                .map(PineconeDatabase::toEmbedding)
                .collect(toSet());
    }

    private static Embedding toEmbedding(Vector vector) {
        val text = vector.getMetadata()
                .getFieldsMap()
                .get(METADATA_ORIGINAL_TEXT)
                .getStringValue();

        return new Embedding(text, toDoubles(vector.getValuesList()));
    }

    private static List<Float> toFloats(Embedding embedding) {
        return embedding.getVector().stream()
                .map(Double::floatValue)
                .collect(toList());
    }

    private static List<Double> toDoubles(List<Float> floats) {
        return floats.stream()
                .map(Float::doubleValue)
                .collect(toList());
    }

    private static String createUniqueId() {
        return UUID.randomUUID().toString();
    }
}
