package com.resume.backend.batch;

import com.resume.backend.repository.ResumeAnalysisJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResumeAnalysisListener implements StepExecutionListener {
    private final ResumeAnalysisJobRepository resumeAnalysisJobRepository;

    public ResumeAnalysisListener(ResumeAnalysisJobRepository resumeAnalysisJobRepository) {
        this.resumeAnalysisJobRepository = resumeAnalysisJobRepository;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("After step");
        log.info("Read Count {}", stepExecution.getReadCount());
        log.info("Write Count {}" ,stepExecution.getWriteCount());
        log.info("Read Skip Count {}", stepExecution.getReadSkipCount());
        log.info("Process Skip {}",stepExecution.getProcessSkipCount());
        JobParameters jobParameters = stepExecution.getJobExecution().getJobParameters();
        String jobId = jobParameters.getString("jobId");
        log.info("job id {}", jobId);

        return stepExecution.getExitStatus();
    }

}
