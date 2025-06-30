package com.resume.backend.dtos;

import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
public class DashboardDto {
    private long totalResumes;
    private  Double totalResumePercentage;
    private long canditateScanned;
    private int totalCanditatePercentage;
    private int bestMatch;
    private double averageExperience;
    private ResumeAnalysisEntity resumeAnalysisEntity;

    public long getTotalResumes() {
        return totalResumes;
    }

    public void setTotalResumes(long totalResumes) {
        this.totalResumes = totalResumes;
    }

    public long getCanditateScanned() {
        return canditateScanned;
    }

    public void setCanditateScanned(long canditateScanned) {
        this.canditateScanned = canditateScanned;
    }

    public int getBestMatch() {
        return bestMatch;
    }

    public void setBestMatch(int bestMatch) {
        this.bestMatch = bestMatch;
    }

    public double getAverageExperience() {
        return averageExperience;
    }

    public void setAverageExperience(double averageExperience) {
        this.averageExperience = averageExperience;
    }

    public double getTotalResumePercentage() {
        return totalResumePercentage;
    }

    public void setTotalResumePercentage(double totalResumePercentage) {
        this.totalResumePercentage = totalResumePercentage;
    }

    public int getTotalCanditatePercentage() {
        return totalCanditatePercentage;
    }

    public void setTotalCanditatePercentage(int totalCanditatePercentage) {
        this.totalCanditatePercentage = totalCanditatePercentage;
    }
}
