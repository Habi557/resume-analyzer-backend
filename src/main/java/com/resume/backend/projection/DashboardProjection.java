package com.resume.backend.projection;

public interface DashboardProjection {
    Integer getTotalResumes();
    Integer getTotalAnalysed();
    Integer getTotalNotAnalysed();
    Integer getCandidatesScreened();
    Integer getBestMatch();
    Double getAverageExperience();
}
