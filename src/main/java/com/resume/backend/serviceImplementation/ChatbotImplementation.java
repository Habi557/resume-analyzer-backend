package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.ChatbotResponse;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.services.ChatbotService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatbotImplementation implements ChatbotService {
    private ChatClient chatClient;
    private ResumeHelper resumeHelper;

    public  ChatbotImplementation(ChatClient chatClient, ResumeHelper resumeHelper) {
        this.chatClient = chatClient;
        this.resumeHelper=resumeHelper;
    }
    @Override
    public String chatbotCall(String query) {
        String content = this.chatClient.prompt(query).call().chatResponse().getResult().getOutput().getContent();
        String validJson = this.resumeHelper.extractJson(content);
        return validJson;
    }
}
