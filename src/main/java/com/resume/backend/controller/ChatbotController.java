package com.resume.backend.controller;

import com.resume.backend.dtos.ChatbotResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {
    @PostMapping("/query")
    public List<ChatbotResponse> chatbot(@RequestBody Map<String, String> body){
        String query = body.get("query");
        System.out.println("query "+query);
        return null;

    }
}
