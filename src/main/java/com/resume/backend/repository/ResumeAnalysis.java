package com.resume.backend.repository;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.ResumeAnalysisEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeAnalysis  extends JpaRepository<ResumeAnalysisEntity,Long>  , JpaSpecificationExecutor<ResumeAnalysisEntity> {
    @Query("SELECT ra FROM ResumeAnalysisEntity ra WHERE ra.resume.user.username = :username")
    List<ResumeAnalysisEntity> getAllUsersAnalysizedResumes(@Param("username") String username);
    @Query("SELECT DISTINCT ra FROM ResumeAnalysisEntity ra " +
            "JOIN ra.resume r " +
            "JOIN r.skills s " +
            "WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :skillName, '%'))")
    Page<ResumeAnalysisEntity> findByResume_Skills_NameAndOrderByLatest(@Param("skillName") String skillName, Pageable pageable);
}
