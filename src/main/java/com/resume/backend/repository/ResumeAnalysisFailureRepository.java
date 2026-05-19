package com.resume.backend.repository;

import com.resume.backend.entity.ResumeAnalysisFailure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeAnalysisFailureRepository extends JpaRepository<ResumeAnalysisFailure, Long> {
}
