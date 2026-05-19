package com.resume.backend.services;

import com.resume.backend.dtos.ResumeAnalysisDTO;

import java.util.List;

public interface ResumeAnalysisService {
    String analysisResumeWithJd(String text, boolean scanAllresumesIsChecked) ;

}
