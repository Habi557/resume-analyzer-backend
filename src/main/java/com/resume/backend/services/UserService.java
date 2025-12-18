package com.resume.backend.services;

import com.resume.backend.dtos.ResumeAnalysisDTO;

import java.util.List;

public interface UserService {
    List<ResumeAnalysisDTO> getAllUsersAnalysizedResumes(String username);
}
