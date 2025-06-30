package com.resume.backend.controller;

import com.resume.backend.dtos.ChatbotResponse;
import com.resume.backend.services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {
    @Autowired
    ChatbotService chatbotService;
    @GetMapping("/query")
    public String chatbot(@RequestParam("userQuery") String body){
      //  String query = body.get("query");
        System.out.println("query "+body);
        return chatbotService.chatbotCall(body);

    }
}
