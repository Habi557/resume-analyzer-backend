package com.resume.backend.repository;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.ResumeAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeAnalysis  extends JpaRepository<ResumeAnalysisEntity,Long> {
    @Query("SELECT ra FROM ResumeAnalysisEntity ra WHERE ra.resume.user.username = :username")
    List<ResumeAnalysisEntity> getAllUsersAnalysizedResumes(String username);
}
