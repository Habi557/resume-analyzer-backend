package com.resume.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_jobs", indexes = {
        @Index(name = "idx_job_id", columnList = "jobId"),
        @Index(name = "idx_job_status", columnList = "status")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisJobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String jobId;

    @Column(nullable = true, columnDefinition = "LONGTEXT")
    private String jobRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;   // PENDING | RUNNING | COMPLETED | FAILED

    private int totalResumes;
    private int processedResumes;
    private int failedResumes;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
