package com.resume.backend.controller;

import com.resume.backend.dtos.ResumeAnalysisPollingDto;
import com.resume.backend.entity.JobStatus;
import com.resume.backend.services.PollingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analyze/status")
public class ResumeAnalysisPolling {
    private final PollingService pollingService;
    public ResumeAnalysisPolling(PollingService pollingService) {
        this.pollingService = pollingService;
    }
    @GetMapping("/{jobId}")
    public ResponseEntity<ResumeAnalysisPollingDto> getPolling(@PathVariable String jobId) {
        ResumeAnalysisPollingDto polling = pollingService.getPolling(jobId);
        return ResponseEntity.ok(polling);
    }
}
