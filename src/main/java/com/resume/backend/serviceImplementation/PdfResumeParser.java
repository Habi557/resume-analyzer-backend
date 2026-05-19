package com.resume.backend.serviceImplementation;

import com.resume.backend.exceptions.ResumeParsingException;
import com.resume.backend.services.ResumeParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component("pdf")
public class PdfResumeParser implements ResumeParser {
    @Override
    public String extractText(InputStream inputStream) {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            stripper.setLineSeparator("\n");
            String text = stripper.getText(document);
            document.close();
            return normalize(text);
        } catch (IOException e) {
            throw new ResumeParsingException("Failed to parse PDF file");
        }
    }
    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[•●▪]", "-")
                .replaceAll("\\r", "")
                .replaceAll("\\n{2,}", "\n")
                .trim();
    }
}
