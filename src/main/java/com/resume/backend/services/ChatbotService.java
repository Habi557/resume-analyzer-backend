package com.resume.backend.services;

import com.resume.backend.dtos.ChatbotResponse;

import java.util.List;

public interface ChatbotService {
    List<ChatbotResponse> chatbotCall(String query);
}
