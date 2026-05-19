package com.resume.backend.serviceImplementation;

import com.resume.backend.exceptions.ResumeParsingException;
import com.resume.backend.services.ResumeParser;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
@Component("docx")
public class DocxResumeParser implements ResumeParser {
    @Override
    public String extractText(InputStream inputStream) {
        try (XWPFDocument doc = new XWPFDocument(inputStream)) {

            String headerText = extractHeaders(doc);
            String tableText  = extractTables(doc);
            String bodyText   = extractBody(doc);

            return headerText + "\n" + tableText + "\n" + bodyText;
        } catch (IOException e) {
            throw new ResumeParsingException("Failed to parse DOCX file");
        }
    }
    private String extractHeaders(XWPFDocument doc) {
        StringBuilder sb = new StringBuilder();
        for (XWPFHeader header : doc.getHeaderList()) {
            for (XWPFParagraph p : header.getParagraphs()) {
                sb.append(p.getText()).append("\n");
            }
        }
        return sb.toString();
    }
    private String extractTables(XWPFDocument doc) {
        StringBuilder sb = new StringBuilder();
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    sb.append(cell.getText()).append("\n");
                }
            }
        }
        return sb.toString();
    }
    private String extractBody(XWPFDocument doc) {
        StringBuilder sb = new StringBuilder();
        for (XWPFParagraph p : doc.getParagraphs()) {
            sb.append(p.getText()).append("\n");
        }
        return sb.toString();
    }
}
