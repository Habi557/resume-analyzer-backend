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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.net.ConnectException;
import java.util.stream.Collectors;
@Component
public class ResumeHelper {


    public  String extractTextFromPdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
    public String extractTextFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
//    public String extractTextFromDocx(MultipartFile file) throws IOException {
//        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
//            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
//            return extractor.getText();
//        }
//    }
public String extractTextFromDocx(File savedFile) throws IOException {
    try (FileInputStream fis = new FileInputStream(savedFile);
         XWPFDocument doc = new XWPFDocument(fis);
         XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {

        return extractor.getText();
    }
}
    public String extractTextFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {

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
    public String loadPromptTemplate2(String filename) {
        try (InputStream is = new ClassPathResource(filename).getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load prompt template: " + filename, e);
        }
    }
    // put values to prompt
    public  String putValuesToPrompt(String template, Map<String,String> values){
        for(Map.Entry<String,String> entry : values.entrySet()){
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
    public boolean isValidGmail(String email) {
        String regex = "^[\\w.-]+@gmail\\.com$";
        return email.matches(regex);
    }

}
