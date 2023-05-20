package dev.ai4j.document.splitter;

import dev.ai4j.document.Document;
import lombok.Builder;
import lombok.val;
import lombok.var;

import java.util.ArrayList;
import java.util.List;

public class OverlappingDocumentSplitter implements DocumentSplitter {

    private final int chunkSize;
    private final int chunkOverlap;

    @Builder
    public OverlappingDocumentSplitter(int chunkSize, int chunkOverlap) {
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    @Override
    public List<Document> split(Document document) {
        if (document.getContents() == null || document.getContents().isEmpty()) {
            throw new IllegalArgumentException("Document content should not be null or empty");
        }

        val contents = document.getContents();
        val contentLength = contents.length();

        if (chunkSize <= 0 || chunkOverlap < 0 || chunkSize <= chunkOverlap) {
            throw new IllegalArgumentException(String.format("Invalid chunkSize (%s) or chunkOverlap (%s)", chunkSize, chunkOverlap));
        }

        val result = new ArrayList<Document>();
        if (contentLength <= chunkSize) {
            result.add(document);
        } else {
            for (var i = 0; i < contentLength - chunkOverlap; i += chunkSize - chunkOverlap) {
                val endIndex = Math.min(i + chunkSize, contentLength);
                val chunk = contents.substring(i, endIndex);
                result.add(Document.from(chunk));
                if (endIndex == contentLength) {
                    break;
                }
            }
        }

        return result;
    }
}
