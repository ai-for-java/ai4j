package dev.ai4j.schema.document.loader;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TextFileLoaderTest {

    @Test
    void should_load_text_file_with_utf8_charset_by_default() {

        val loader = TextFileLoader.builder()
                .absolutePathToTextFile(System.getProperty("user.dir") + "/src/test/java/dev/ai4j/schema/document/loader/test-file-utf8.txt")
                .build();

        val document = loader.load();

        assertThat(document.getContents()).isEqualTo("test\ncontent");
    }

    @Test
    void should_load_text_file_with_specified_charset() {

        val loader = TextFileLoader.builder()
                .absolutePathToTextFile(System.getProperty("user.dir") + "/src/test/java/dev/ai4j/schema/document/loader/test-file-iso-8859-1.txt")
                .charset(ISO_8859_1)
                .build();

        val document = loader.load();

        assertThat(document.getContents()).isEqualTo("test\ncontent");
    }

    @Test
    void should_fail_to_load_not_existing_file() {

        val loader = TextFileLoader.builder()
                .absolutePathToTextFile(System.getProperty("user.dir") + "banana")
                .build();

        assertThatThrownBy(loader::load).isInstanceOf(FileNotFoundException.class);
    }
}