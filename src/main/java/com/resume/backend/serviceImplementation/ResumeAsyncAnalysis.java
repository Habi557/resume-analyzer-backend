package com.resume.backend.serviceImplementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.ResumeResult;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.dtos.SkillDto;
import com.resume.backend.entity.JobStatus;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.ResumeAnalysisFailure;
import com.resume.backend.exceptions.AiNotRespondingException;
import com.resume.backend.helperclass.AiApis;
import com.resume.backend.helperclass.ConvertingEntityToDtos;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeAnalysisFailureRepository;
import com.resume.backend.repository.ResumeAnalysisJobRepository;
import com.resume.backend.repository.ResumeRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ResumeAsyncAnalysis {

    private final ResumeRepository resumeRepository;
    private final ConvertingEntityToDtos convertingEntityToDtos;
    private final ModelMapper modelMapper;
    private final ResumeAnalysis resumeAnalysis;
    private final ResumeHelper resumeHelper;
    private final AiApis aiApis;
    private final ObjectMapper objectMapper;
    private final ResumeAnalysisJobRepository resumeJobRepository;
    private final ResumeAnalysisFailureRepository resumeAnalysisFailureRepository;

    @Lazy
    @Autowired // ✅ inject self so @Transactional methods go through the proxy
    private ResumeAsyncAnalysis self;
    @Value("${resume.analysis.ai-concurrency}")
    private Integer aiConcurrency;
    private String template;
    @Value("${resume.analysis.page-size}")
    private Integer pageSize;

    ResumeAsyncAnalysis(ResumeRepository resumeRepository,
                        ConvertingEntityToDtos convertingEntityToDtos,
                        ModelMapper modelMapper,
                        ResumeAnalysis resumeAnalysis,
                        ResumeHelper resumeHelper,
                        AiApis aiApis,
                        ResumeAnalysisJobRepository resumeJobRepository,
                        ObjectMapper objectMapper,
                        ResumeAnalysisFailureRepository resumeAnalysisFailureRepository) {
        this.resumeRepository = resumeRepository;
        this.convertingEntityToDtos = convertingEntityToDtos;
        this.modelMapper = modelMapper;
        this.resumeAnalysis = resumeAnalysis;
        this.resumeHelper = resumeHelper;
        this.aiApis = aiApis;
        this.resumeJobRepository = resumeJobRepository;
        this.objectMapper = objectMapper;
        this.resumeAnalysisFailureRepository = resumeAnalysisFailureRepository;
    }
    @PostConstruct
    public void init() {
         template = resumeHelper.loadPromptTemplate2("prompts/resumeScreeningMatcher.txt");

    }

    // =====================================================================
    // MAIN ASYNC METHOD — NO @Transactional here (child methods have their own)
    // =====================================================================

    @Async("resumeAnalysisExecutor")
    public void resumeScreenAI(String jobRole, String jobId, boolean scanAllresumesIsChecked) {
        updateJobStatus(jobId, JobStatus.RUNNING, 0, 0);
        log.debug("resumeScreenAI started on thread={}, jobId={}",
                Thread.currentThread().getName(), jobId);

        int page = 0;
        //int pageSize = 10;
        int totalProcessed = 0;
        int totalFailed = 0;
        int batchSize = 5;

        try {
            boolean hasNext;
            do {
                Page<ResumeTempDto> resumePage = self.fetchResumePage(page, pageSize, scanAllresumesIsChecked);

                if (resumePage.isEmpty()) break;
                hasNext = resumePage.hasNext();

//                if (page == 0) {
//                    // ✅ Commits immediately in its own transaction — visible in DB right away
//                    self.updateTotalCount(jobId, (int) resumePage.getTotalElements());
//                }
                page++;

                List<ResumeTempDto> resumeInputs = resumePage.getContent();
                AtomicInteger pageFailed = new AtomicInteger(0);
                AtomicInteger pageSuccess = new AtomicInteger(0);

                try (ExecutorService executor = Executors.newFixedThreadPool(aiConcurrency)) {
                    CompletionService<ResumeResult> completionService =
                            new ExecutorCompletionService<>(executor);
                    // Submit all AI tasks in parallel
                    resumeInputs.forEach(dto ->
                            completionService.submit(() -> safeAnalyze(dto, jobRole,jobId))
                    );

                    List<ResumeResult> batchBuffer = new ArrayList<>();
                    int totalSubmitted = resumeInputs.size();
                    int totalCollected = 0; // ✅ track separately from loop index

                    for (int i = 0; i < totalSubmitted; i++) {
                        try {
                            //Future<ResumeResult> future = completionService.poll(30, TimeUnit.SECONDS);
                            Future<ResumeResult> future = completionService.take();

//                            if (future == null) {
//                                // Timeout — no result came back in 30s
//                                log.warn("Timeout waiting for resume result at index={}, jobId={}", i, jobId);
//                                pageFailed.incrementAndGet();
//                                totalCollected++; // ✅ count timeouts too
//                                continue;
//                            }

                            ResumeResult result = future.get();
                            batchBuffer.add(result);
                            totalCollected++;

                            // ✅ Flush when batch full OR truly last collected result
                            boolean isLast = (totalCollected == totalSubmitted);
                            log.debug("Flushing batch for jobId={}, batchBuffer.size()={},isLast", jobId,batchBuffer.size(),isLast);
                            log.debug("TotalCollected ={},TotalSubmitted={}",totalCollected,totalSubmitted);
                            if (batchBuffer.size() >= batchSize || isLast) {

                                flushBatch(batchBuffer, jobId, pageFailed, pageSuccess);
                                batchBuffer.clear();
                            }

                        } catch (ExecutionException e) {
                            Throwable cause = e.getCause();
                            totalCollected++;

                            if (cause instanceof AiNotRespondingException) {
                                log.warn("AI service down for resume index={}, jobId={}: {}",
                                        i, jobId, cause.getMessage());
                            } else {
                                log.error("Resume analysis failed at index={}, jobId={}",
                                        i, jobId, cause);
                            }
                            pageFailed.incrementAndGet();

                        } catch (InterruptedException e) {
                            // ✅ Never swallow InterruptedException
                            Thread.currentThread().interrupt();
                            log.error("Thread interrupted during resume collection, jobId={}", jobId);
                            updateJobStatus(jobId, JobStatus.FAILED, totalProcessed, totalFailed);
                            return;
                        }
                    }

                    // ✅ Safety flush — handles edge case where last N all timed out
                    if (!batchBuffer.isEmpty()) {
                        flushBatch(batchBuffer, jobId, pageFailed, pageSuccess);
                        batchBuffer.clear();
                    }
                }

                totalProcessed += pageSuccess.get();
                totalFailed += pageFailed.get();

                // ✅ Commits immediately — progress visible after each page
                self.updateJobProgress(jobId, totalProcessed, totalFailed);

            } while (hasNext);

        } catch (Exception e) {
            log.error("Fatal error processing resumes for jobId={}", jobId, e);
            updateJobStatus(jobId, JobStatus.FAILED, totalProcessed, totalFailed);
            return;
        }

        // ✅ Final status based on outcome
        if (totalProcessed == 0 && totalFailed > 0) {
            updateJobStatus(jobId, JobStatus.AI_SERVICE_DOWN, totalProcessed, totalFailed);
            log.error("Job {} — ALL resumes failed, AI likely down", jobId);
        } else if (totalFailed > 0) {
            updateJobStatus(jobId, JobStatus.COMPLETED_WITH_FAILURES, totalProcessed, totalFailed);
            log.warn("Job {} completed with {} failures", jobId, totalFailed);
        } else {
            updateJobStatus(jobId, JobStatus.COMPLETED, totalProcessed, totalFailed);
            log.info("Job {} completed successfully — {} resumes processed", jobId, totalProcessed);
        }
    }

    // =====================================================================
    // FLUSH BATCH — saves every 5 completed resumes to DB
    // =====================================================================

    private void flushBatch(List<ResumeResult> batch, String jobId,
                            AtomicInteger pageFailed, AtomicInteger pageSuccess) {

        List<ResumeAnalysisDTO> successDtos = batch.stream()
                .filter(ResumeResult::success)
                .map(ResumeResult::dto)
                .toList();

        List<ResumeTempDto> failedDtos = batch.stream()
                .filter(r -> !r.success())
                .map(r -> r.dto() != null ? r.dto().getResume() : null)
                .filter(Objects::nonNull)
                .toList();

        if (!successDtos.isEmpty()) {
            try {
                self.saveBatchResults(successDtos);
                pageSuccess.addAndGet(successDtos.size());
                log.info("✅ Batch saved — {} resumes", successDtos.size());
            } catch (Exception e) {
                // Batch save failed — count them as failed
                log.error("❌ Batch save failed, marking {} resumes as failed", successDtos.size(), e);
                pageFailed.addAndGet(successDtos.size());
            }
        }

        if (!failedDtos.isEmpty()) {
            try {
                self.saveFailures(failedDtos, jobId, batch);
                pageFailed.addAndGet(failedDtos.size());
                log.warn("⚠️ Saved {} failure records", failedDtos.size());
            } catch (Exception e) {
                log.error("❌ Failed to save failure records", e);
                pageFailed.addAndGet(failedDtos.size());
            }
        }
    }

    // =====================================================================
    // DB OPERATIONS — each has its own @Transactional (commits independently)
    // =====================================================================

    @Transactional(readOnly = true)
    public Page<ResumeTempDto> fetchResumePage(int page, int pageSize, boolean scanAll) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Page<Resume> chunksOfResumes = scanAll
                ? resumeRepository.findAll(pageRequest)
                : resumeRepository.findByScanAllresumesIsCheckedFalse(pageRequest);

        // ✅ Convert to DTOs inside transaction while session is open
        return chunksOfResumes.map(convertingEntityToDtos::convertResumeDto);
    }

    @Transactional
    public void saveBatchResults(List<ResumeAnalysisDTO> successDtos) {
        // ✅ Fetch all resume entities in ONE query
        List<Long> ids = successDtos.stream()
                .map(dto -> dto.getResume().getId())
                .toList();

        Map<Long, Resume> resumeMap = resumeRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(Resume::getId, r -> r));

        // Build analysis entities
        List<ResumeAnalysisEntity> entities = successDtos.stream()
                .map(dto -> {
                    Resume resume = resumeMap.get(dto.getResume().getId());
                    if (resume == null) {
                        throw new RuntimeException("Resume not found: " + dto.getResume().getId());
                    }
                    ResumeAnalysisEntity entity = modelMapper.map(dto, ResumeAnalysisEntity.class);
                    entity.setId(null);
                    entity.setAnalysizedTime(LocalDateTime.now());
                    entity.setResume(resume);
                    return entity;
                })
                .toList();

        // Mark all as scanned
        resumeMap.values().forEach(r -> r.setScanAllresumesIsChecked(true));

        // ✅ Only 2 DB round trips for entire batch
        resumeRepository.saveAll(new ArrayList<>(resumeMap.values()));
        resumeAnalysis.saveAll(entities);
    }

    @Transactional
    public void saveFailures(List<ResumeTempDto> failedDtos, String jobId, List<ResumeResult> results) {
        List<ResumeAnalysisFailure> failures = failedDtos.stream()
                .map(resumeTempDto -> {
                    Resume resume = modelMapper.map(resumeTempDto, Resume.class);
                    return ResumeAnalysisFailure.builder()
                            .resume(resume)
                            .jobId(jobId)
                            .build();
                })
                .collect(Collectors.toList());

        resumeAnalysisFailureRepository.saveAll(failures);
    }

    // =====================================================================
    // JOB STATUS UPDATES — each commits independently
    // =====================================================================

    @Transactional
    public void updateJobStatus(String jobId, JobStatus status, int processed, int failed) {
        resumeJobRepository.findByJobId(jobId).ifPresent(job -> {
            job.setStatus(status);
            //if (total >= 0)     job.setTotalResumes(total);
            if (processed >= 0) job.setProcessedResumes(processed);
            if (failed >= 0)    job.setFailedResumes(failed);
            if (status == JobStatus.COMPLETED || status == JobStatus.FAILED
                    || status == JobStatus.COMPLETED_WITH_FAILURES
                    || status == JobStatus.AI_SERVICE_DOWN)
                job.setCompletedAt(LocalDateTime.now());
            resumeJobRepository.save(job);
        });
    }

    @Transactional
    public void updateJobProgress(String jobId, int processed, int failed) {
        resumeJobRepository.findByJobId(jobId).ifPresent(job -> {
            job.setProcessedResumes(processed);
            job.setFailedResumes(failed);
            resumeJobRepository.save(job);
        });
    }

    @Transactional
    public void updateTotalCount(String jobId, int total) {
        resumeJobRepository.findByJobId(jobId).ifPresent(job -> {
            job.setTotalResumes(total);
            resumeJobRepository.save(job);
        });
    }

    @Transactional
    public void updateJobStatus(String jobId, JobStatus status) {
        resumeJobRepository.findByJobId(jobId).ifPresent(job -> {
            job.setStatus(status);
            resumeJobRepository.save(job);
        });
    }

    // =====================================================================
    // AI ANALYSIS
    // =====================================================================

    private ResumeAnalysisDTO analyzeSingleResumeAsync(ResumeTempDto resume, String jobRole) {
        try {
            String skills = resume.getSkills()
                    .stream()
                    .map(SkillDto::getName)
                    .collect(Collectors.joining(", "));

            String tempResumeText = """
                    Name: %s
                    Skills: %s
                    Experience: %s years
                    Address: %s
                    """.formatted(
                    resume.getName(),
                    skills,
                    resume.getYearsOfExperience(),
                    resume.getAddress()
            );

            //String template = resumeHelper.loadPromptTemplate2("prompts/resumeScreeningMatcher.txt");
            String prompt = resumeHelper.putValuesToPrompt(
                    template,
                    Map.of("resumeText", tempResumeText, "jobRole", jobRole)
            );

            String aiResponse = aiApis.callAiService(prompt);
            String validJson = resumeHelper.extractJson(aiResponse);

           // log.debug("AI response JSON: {}", validJson);

            ResumeAnalysisDTO dto = objectMapper.readValue(validJson, ResumeAnalysisDTO.class);
            dto.setResume(resume);

            //log.debug("Parsed DTO: {}", dto);
            return dto;

        } catch (Exception e) {
            throw new RuntimeException("AI processing failed for resume id=" + resume.getId(), e);
        }
    }

    private ResumeResult safeAnalyze(ResumeTempDto resume, String jobRole,String jobId) {
        try {
            return ResumeResult.ok(analyzeSingleResumeAsync(resume, jobRole));
        } catch (Exception e) {
            // ✅ Unwrap — RestClientException may be buried inside RuntimeException
            Throwable cause = e.getCause() != null ? e.getCause() : e;

            if (cause instanceof RestClientException) {
                log.warn("AI service down for resumeId={}: {}", resume.getId(), cause.getMessage());
                return ResumeResult.fail("AI service is down, try again later");
            }

            log.warn("Failed to analyze resumeId={} name={}: failed reason{}",
                    resume.getId(), resume.getName(), e.getMessage());

            resumeJobRepository.findByJobId(jobId).ifPresent(job -> {
                job.setFailedResumes(job.getFailedResumes() + 1);

                resumeJobRepository.save(job);
                log.debug("saved the failed resume {}",job);
            });
            return ResumeResult.fail(e.getMessage());
        }
    }
}