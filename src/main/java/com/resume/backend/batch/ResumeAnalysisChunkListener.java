package com.resume.backend.batch;

import com.resume.backend.entity.ResumeAnalysisJobEntity;
import com.resume.backend.repository.ResumeAnalysisJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResumeAnalysisChunkListener implements ChunkListener {
    private final ResumeAnalysisJobRepository resumeAnalysisJobRepository;

    public ResumeAnalysisChunkListener(ResumeAnalysisJobRepository resumeAnalysisJobRepository) {
        this.resumeAnalysisJobRepository = resumeAnalysisJobRepository;
    }

    @Override
    public void beforeChunk(ChunkContext context) {
        System.out.println("Chunk Started");
    }
    @Override
    public void afterChunk(ChunkContext context) {
        String jobId = context.getStepContext()
                .getStepExecution()
                .getJobParameters()
                .getString("jobId");

      StepExecution stepExecution= context.getStepContext().getStepExecution();
        log.info("Job id: {}", jobId);
        ResumeAnalysisJobEntity resumeAnalysisJobEntity = resumeAnalysisJobRepository.findByJobId(jobId).orElseThrow(() -> new RuntimeException("Job not found with job id : "+jobId));
        resumeAnalysisJobEntity.setProcessedResumes((int)stepExecution.getWriteCount());
        resumeAnalysisJobEntity.setFailedResumes((int)stepExecution.getWriteSkipCount());
        resumeAnalysisJobRepository.save(resumeAnalysisJobEntity);
    }
}
