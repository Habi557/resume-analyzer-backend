package com.resume.backend.batch;

import com.resume.backend.entity.JobStatus;
import com.resume.backend.entity.ResumeAnalysisJobEntity;
import com.resume.backend.repository.ResumeAnalysisJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@Slf4j
public class ResumeAnalysisJobCompletionListener  implements JobExecutionListener {
    private final ResumeAnalysisJobRepository resumeAnalysisJobRepository;
    public ResumeAnalysisJobCompletionListener(ResumeAnalysisJobRepository resumeAnalysisJobRepository) {
        this.resumeAnalysisJobRepository = resumeAnalysisJobRepository;
    }
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Before job");

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("After job Completed");
        String jobId = jobExecution.getJobParameters().getString("jobId");
        ResumeAnalysisJobEntity resumeAnalysisJobEntity = resumeAnalysisJobRepository.findByJobId(jobId).orElseThrow(() -> new RuntimeException("Job not found with job id : "+jobId));
        resumeAnalysisJobEntity.setCompletedAt(LocalDateTime.now());
        resumeAnalysisJobEntity.setStatus(JobStatus.COMPLETED);
        resumeAnalysisJobRepository.save(resumeAnalysisJobEntity);

    }
}
