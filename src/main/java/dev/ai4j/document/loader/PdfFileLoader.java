package dev.ai4j.document.loader;

import dev.ai4j.document.Document;
import dev.ai4j.document.DocumentLoader;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

@AllArgsConstructor
public class PdfFileLoader implements DocumentLoader {

    private final String absolutePathToPdfFile;

    @Override
    @SneakyThrows
    public Document load() {
        val pdfFile = new File(absolutePathToPdfFile);
        val pdfDocument = PDDocument.load(pdfFile);
        val stripper = new PDFTextStripper();
        val text = stripper.getText(pdfDocument);
        pdfDocument.close();
        return new Document(text);
    }
}
