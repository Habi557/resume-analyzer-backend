package com.resume.backend.serviceImplementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.dtos.SkillDto;
import com.resume.backend.entity.JobStatus;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.ResumeAnalysisJobEntity;
import com.resume.backend.exceptions.JsonProcessingRuntimeException;
import com.resume.backend.helperclass.AiApis;
import com.resume.backend.helperclass.ConvertingEntityToDtos;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeAnalysisJobRepository;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.ResumeAnalysisService;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
@Service
public class ResumeAnalysisServiceImpl implements ResumeAnalysisService {

    private final ResumeAnalysisJobRepository resumeJobRepository;
    private final ResumeAsyncAnalysis resumeAsyncAnalysis;
    private final ResumeRepository resumeRepository;

    public ResumeAnalysisServiceImpl(ResumeAnalysisJobRepository resumeJobRepository, ResumeAsyncAnalysis resumeAsyncAnalysis,ResumeRepository resumeRepository) {
        this.resumeJobRepository = resumeJobRepository;
        this.resumeAsyncAnalysis = resumeAsyncAnalysis;
        this.resumeRepository=resumeRepository;
     }
    @CacheEvict(value = "getAllDashboardDetails", allEntries = true)
    public String analysisResumeWithJd(String jobRole, boolean scanAllresumesIsChecked){

        String jobId=UUID.randomUUID().toString();
        ResumeAnalysisJobEntity build = ResumeAnalysisJobEntity.builder().jobId(jobId)
                .jobRole(jobRole)
                .status(JobStatus.PENDING).totalResumes((int)resumeRepository.count()).processedResumes(0).failedResumes(0).createdAt(LocalDateTime.now()).build();
        resumeJobRepository.save(build);
        resumeAsyncAnalysis.resumeScreenAI(jobRole,jobId,scanAllresumesIsChecked);
        return jobId;


    }



}
