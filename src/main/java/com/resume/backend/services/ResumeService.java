package com.resume.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.dtos.FileDownloadDataDto;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.projection.ResumeProjection;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ResumeService {
    String generateResumeResponse(String userResumeDeatils);
     Resume uploadResume(String username, MultipartFile file) ;

    List<ResumeAnalysisDTO> resumeScreen(String text, boolean scanAllresumesIsChecked) ;

    List<ResumeAnalysisDTO> getAllAnalysiedResumes(int pageNo, int pageSize);

    DashboardDto getAllDashboardDetails();

    FileDownloadDataDto dowloadResume(long resumeId);

    List<ResumeProjection> getAllResumes(int pageNo,int pagesize);

    List<String> getSuggestions(String query);

    List<Resume> findResumesBySkillName(String lowerCase, int currentPage, int pageSize);
}
