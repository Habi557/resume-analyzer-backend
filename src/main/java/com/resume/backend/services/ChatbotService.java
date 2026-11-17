package com.resume.backend.services;

import com.resume.backend.dtos.ChatbotResponse;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;

import java.util.List;

public interface ChatbotService {
    List<ResumeAnalysisDTO> getAnswer(String message);
    List<ResumeAnalysisDTO> getAnswerV2(String message, int size);
}
