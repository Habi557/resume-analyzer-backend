package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.ResumeAnalysisPollingDto;
import com.resume.backend.repository.ResumeAnalysisJobRepository;
import com.resume.backend.services.PollingService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PollingServiceImpl implements PollingService {
    private final ResumeAnalysisJobRepository resumeAnalysisJobRepository;
    private final ModelMapper modelMapper;
    PollingServiceImpl(ResumeAnalysisJobRepository resumeAnalysisJobRepository,ModelMapper modelMapper) {
        this.resumeAnalysisJobRepository = resumeAnalysisJobRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public ResumeAnalysisPollingDto getPolling(String jobId) {
        log.info("Getting polling for job id: {}", jobId);
        ResumeAnalysisPollingDto resumeAnalysisPollingDto = resumeAnalysisJobRepository.findByJobId(jobId).map(job -> modelMapper.map(job, ResumeAnalysisPollingDto.class))
                .orElseThrow(() -> new RuntimeException("Job not found"));

        return resumeAnalysisPollingDto;
    }
}
