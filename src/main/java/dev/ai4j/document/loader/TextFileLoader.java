package dev.ai4j.document.loader;

import dev.ai4j.document.Document;
import dev.ai4j.document.DocumentLoader;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.val;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        val fileContents = new String(Files.readAllBytes(Paths.get(absolutePathToTextFile)), charset);
        return Document.from(fileContents);
    }
}
