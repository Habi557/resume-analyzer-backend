package com.resume.backend.controller;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/sample")
public class AiController {
    private final OllamaChatModel chatClient;
    private ChatClient chatClient2;

    public AiController(OllamaChatModel chatModel,ChatClient chatClient2) {
        this.chatClient = chatModel;
        this.chatClient2=chatClient2;
    }
    @GetMapping("/ai/generate")
    public Map<String,String> generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.chatClient.call(message));
    }
    
    @GetMapping("/process")
    public String processInput(@RequestParam("input") String input) {
        // Call the AI with the provided input and return the response as a string
        return this.chatClient.call(input);
    }
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        String content;

        if (file.getOriginalFilename().endsWith(".pdf")) {
            content = extractTextFromPdf(file);
        } else if (file.getOriginalFilename().endsWith(".docx")) {
            content = extractTextFromDocx(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
         return chatClient2.prompt(content)
                 //.system("Your are a Hr now")
                 .system("You are a strict evaluator. Do not use <think> or any explanation.")
                 .user("Does this candidate qualify as a Java Developer? Only respond with true or false.").call().chatResponse().getResult().getOutput().getContent().trim();
//        return chatClient
//                .prompt()
//                .system("You are an AI that analyzes resume content.")
//                .user("Analyze this content: " + content)
//                .call()
//                .content();

    }

    private String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractTextFromDocx(MultipartFile file) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream())) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            return extractor.getText();
        }
    }
}
