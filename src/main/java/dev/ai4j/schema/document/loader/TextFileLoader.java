package dev.ai4j.schema.document.loader;

import com.google.common.io.Files;
import dev.ai4j.schema.document.Document;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TextFileLoader implements DocumentLoader {

    private final String absolutePathToTextFile;
    private final Charset charset;

    @Builder
    public TextFileLoader(String absolutePathToTextFile, Charset charset) {
        this.absolutePathToTextFile = absolutePathToTextFile;
        this.charset = (charset == null) ? UTF_8 : charset;
    }

    @Override
    @SneakyThrows
    public Document load() {
        val fileContents = Files.asCharSource(new File(absolutePathToTextFile), charset).read();
        return Document.from(fileContents);
    }
}
