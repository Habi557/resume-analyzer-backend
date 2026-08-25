package com.resume.backend.batch;

import com.resume.backend.dtos.ResumeResult;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.ResumeStatus;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class ResumeAnalysisBatchProcessing {
    private final ResumeAnalysis resumeAnalysis;
    private ResumeRepository resumeRepository;
    public ResumeAnalysisBatchProcessing(ResumeAnalysis resumeAnalysis,ResumeRepository resumeRepository ) {
        this.resumeAnalysis = resumeAnalysis;
        this.resumeRepository = resumeRepository;
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Resume> resumeReader(EntityManagerFactory emf, @Value("#{jobParameters['scanAllresumesIsChecked']}") String scanAllresumesIsChecked){
        log.info("Reader started");
        Boolean scanAllresumes = Boolean.valueOf(scanAllresumesIsChecked);
        log.info("Scan all resumes: {}", scanAllresumes);
        JpaPagingItemReader<Resume> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(emf);
        reader.setQueryString("""
                select r from Resume r
                where r.scanAllresumesIsChecked = :scanAllresumes
                AND r.status = :status
                """);
        reader.setPageSize(5);
        reader.setParameterValues(Map.of( "status", ResumeStatus.valueOf("UPLOADED"),"scanAllresumes", scanAllresumes));
        return reader;

    }
    @Bean
    public ItemWriter<ResumeAnalysisEntity> writer() {
        return chunk -> {
            log.info("Writer called");
            log.info("Chunk size: {}", chunk.size());
            List<Resume> list = chunk.getItems()
                    .stream()
                    .map(ResumeAnalysisEntity::getResume)
                    .peek(resume -> resume.setScanAllresumesIsChecked(true)).toList();
            resumeRepository.saveAll(list);

            chunk.forEach(item -> log.info("{}", item));
            resumeAnalysis.saveAll(chunk);

        };
    }
    @Bean
    public Step analyzeStep(JobRepository repository,
                            PlatformTransactionManager txManager,
                            JpaPagingItemReader<Resume> reader,
                            ResumeProcessor processor,
                            ResumeAnalysisChunkListener listener,
                            ItemWriter<ResumeAnalysisEntity> writer) {

        return new StepBuilder("analyzeStep", repository)

                .<Resume, ResumeAnalysisEntity>chunk(2, txManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(listener)
                .faultTolerant()
                .skip(RuntimeException.class)
                .skipLimit(100)
                //.retry(RuntimeException.class)
                //.retryLimit(3)

                .build();
    }
    @Bean
    public Job resumeAnalysisJob(JobRepository repository,
                                 Step analyzeStep, ResumeAnalysisJobCompletionListener completionListener) {

        return new JobBuilder("resumeAnalysisJob", repository)
                .listener(completionListener)
                .start(analyzeStep)
                .build();
    }
}
