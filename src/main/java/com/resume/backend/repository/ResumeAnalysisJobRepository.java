package com.resume.backend.repository;

import com.resume.backend.entity.ResumeAnalysisJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeAnalysisJobRepository extends JpaRepository<ResumeAnalysisJobEntity, Long> {
    Optional<ResumeAnalysisJobEntity> findByJobId(String jobId);
}
