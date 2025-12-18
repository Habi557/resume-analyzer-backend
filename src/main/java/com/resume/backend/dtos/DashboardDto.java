package com.resume.backend.dtos;

import com.resume.backend.entity.ResumeAnalysisEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private long totalResumes;
    private long canditateScanned;
    private int bestMatch;
    private double averageExperience;
    private ResumeAnalysisDTO resumeAnalysisDTO;

}
