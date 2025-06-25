package com.resume.backend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ResumeService {
    String generateResumeResponse(String userResumeDeatils);
     Resume uploadResume(Long userId, MultipartFile file) throws IOException;

    List<ResumeAnalysisDTO> resumeScreen(String text, boolean scanAllresumesIsChecked) throws JsonProcessingException;

    List<ResumeAnalysisDTO> getAllAnalysiedResumes(int pageNo, int pageSize);

    DashboardDto getAllDashboardDetails();

    Resource dowloadResume(long resumeId);

    List<Resume> getAllResumes();
}
