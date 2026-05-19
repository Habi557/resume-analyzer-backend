package com.resume.backend.services;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.ResumeProjectionDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.projection.ResumeProjection;

import java.util.List;

public interface ResumeSearchService {
    List<ResumeAnalysisDTO> getAllAnalysiedResumes(int pageNo, int pageSize, String bestMatch, String dir);
    List<ResumeProjectionDto> getAllResumes(int pageNo, int pagesize);
    List<ResumeAnalysisDTO> findResumesBySkillName(String lowerCase, int currentPage, int pageSize);

}
