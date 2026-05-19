package com.resume.backend.services;

import com.resume.backend.dtos.ResumeAnalysisPollingDto;

public interface PollingService {
    ResumeAnalysisPollingDto getPolling(String jobId);
}
