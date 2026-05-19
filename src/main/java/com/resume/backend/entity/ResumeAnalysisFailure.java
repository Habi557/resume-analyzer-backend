package com.resume.backend.entity;

import com.resume.backend.dtos.FailureStatus;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
@Entity
@Table(name = "resume_analysis_failures")
public class ResumeAnalysisFailure {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Resume resume;

    private String jobId;
    private String errorMessage;
    private int retryCount;
    private LocalDateTime failedAt;
    private LocalDateTime lastRetryAt;

    @Enumerated(EnumType.STRING)
    private FailureStatus status; // PENDING_RETRY, RETRY_EXHAUSTED, RESOLVED
}
