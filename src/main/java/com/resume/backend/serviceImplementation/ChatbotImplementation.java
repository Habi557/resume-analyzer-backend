package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.ChatbotResponse;
import com.resume.backend.services.ChatbotService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatbotImplementation implements ChatbotService {
    @Autowired
    private ChatClient chatClient;
    @Override
    public List<ChatbotResponse> chatbotCall(String query) {
        String content = this.chatClient.prompt(query).call().chatResponse().getResult().getOutput().getContent();
        return List.of();
    }
}
