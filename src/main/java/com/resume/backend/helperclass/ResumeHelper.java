package com.resume.backend.helperclass;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
@Component
public class ResumeHelper {


    public  String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    public String extractTextFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            return extractor.getText();
        }
    }
    public  String extractJson(String content) {
        // Remove <think> blocks
        String noThink = content.replaceAll("(?s)<think>.*?</think>", "");

        // Remove triple backticks and any leading markdown
        noThink = noThink.replaceAll("(?s)```json", "")
                .replaceAll("```", "")
                .replaceAll("^\\s*\\*\\*.*?\\*\\*", "") // remove markdown bold
                .trim();

        return noThink;
    }
    // load prompt from classPath
    public  String loadPromptTemplate(String filename) throws IOException {
        Path path = new ClassPathResource(filename).getFile().toPath();
        return Files.readString(path);

    }
    // put values to prompt
    public  String putValuesToPrompt(String template, Map<String,String> values){
        for(Map.Entry<String,String> entry : values.entrySet()){
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}
