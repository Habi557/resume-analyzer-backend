package com.resume.backend.controller;

import com.resume.backend.dtos.ChatbotResponse;
import com.resume.backend.entity.Resume;
import com.resume.backend.services.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {
    @Autowired
    ChatbotService chatbotService;
    @GetMapping("/query")
    public ResponseEntity<String> chatbot(@RequestParam("userQuery") String body){
      //  String query = body.get("query");
        System.out.println("query "+body);
        String answer = chatbotService.getAnswer(body);
        return new ResponseEntity<String>(answer, HttpStatus.OK);

    }
}
