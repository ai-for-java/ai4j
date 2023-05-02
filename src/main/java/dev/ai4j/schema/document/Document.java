package dev.ai4j.schema.document;


import lombok.Value;

@Value
public class Document {

    String contents;

    public static Document from(String contents) {
        return new Document(contents);
    }
}
