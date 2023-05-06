package dev.ai4j.document.splitter;

import dev.ai4j.document.Document;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Arrays;
import java.util.List;

import static dev.ai4j.document.Document.from;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OverlappingDocumentSplitterTest {

    // TODO add more test variety
    @Test
    void testDocumentIsSplit() {
        val sut = new OverlappingDocumentSplitter(4, 2);
        List<Document> result = sut.split(new Document("1234567890"));

        List<Document> expected = Arrays.asList(
                from("1234"),
                from("3456"),
                from("5678"),
                from("7890")
        );

        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({"0,-1", "-1,-1", "-1,0", "0,0", "0,1", "1,-1", "1,1", "1,2"})
    void testIllegalArgumentExceptionWhenChunkSizeAndChunkOverlapMisconfigured(int chunkSize, int chunkOverlap) {
        val sut = new OverlappingDocumentSplitter(chunkSize, chunkOverlap);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> sut.split(new Document("any"))
        );

        assertTrue(thrown.getMessage()
                .contentEquals("Invalid chunkSize (" + chunkSize + ") or chunkOverlap (" + chunkOverlap + ")"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void testNullCase(String documentContent) {
        val sut = new OverlappingDocumentSplitter(4, 2);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> sut.split(new Document(documentContent))
        );

        assertTrue(thrown.getMessage().contentEquals("Document content should not be null or empty"));
    }
}