package com.resume.backend.repository;

import com.resume.backend.entity.ResumeAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeAnalysis  extends JpaRepository<ResumeAnalysisEntity,Long> {
}
