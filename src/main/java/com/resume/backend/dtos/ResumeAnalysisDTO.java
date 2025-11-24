package com.resume.backend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.resume.backend.entity.Resume;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumeAnalysisDTO {
    @JsonProperty("id")
    private long id;
    @JsonProperty("matchPercentage")
    private int matchPercentage;
    @JsonProperty("suggestions")
    private List<String> suggestions;
    @JsonProperty("analysis")
    private String conclusion;
    @JsonProperty("analysizedTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime analysizedTime;
    @JsonProperty("topMatchingSkills")
    private List<String> topMatchingSkills;
    @JsonProperty("resume_id")
    private Resume resume;
    @JsonProperty("interviewDate")
    private String interviewDate;
    @JsonProperty("interviewTime")
    private String interviewTime;
    @JsonProperty("interviewMode")
    private  String interviewMode;
    @JsonProperty("selectedStatus")
    private String selectedStatus;

    public ResumeAnalysisDTO(Long id, int matchPercentage, List<String> suggestions,
                             String conclusion, LocalDateTime analysizedTime,
                             List<String> topMatchingSkills, Resume resume,
                             String interviewDate, String interviewTime,
                             String selectedStatus) {
        this.id = id;
        this.matchPercentage = matchPercentage;
        this.suggestions = suggestions;
        this.conclusion = conclusion;
        this.analysizedTime = analysizedTime;
        this.topMatchingSkills = topMatchingSkills;
        this.resume = resume;
        this.interviewDate = interviewDate;
        this.interviewTime = interviewTime;
        this.selectedStatus = selectedStatus;
    }
}
