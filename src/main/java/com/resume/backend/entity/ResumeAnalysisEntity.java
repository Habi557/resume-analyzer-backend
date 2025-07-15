package com.resume.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.resume.backend.helperclass.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "ResumeAnalysisData")
public class ResumeAnalysisEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="matchPercentage")
    private int matchPercentage;
    @Lob
    @Convert(converter = StringListConverter.class)
    @Column(name = "suggestions",columnDefinition = "LONGTEXT")
    private List<String> suggestions;
    @Lob
    @Column(name = "analysis",columnDefinition = "LONGTEXT")
    private String conclusion;
    @Column(name = "analysizedtime")
    private LocalDateTime analysizedTime;
    @Column(name = "topMatchingSkills")
    private List<String> topMatchingSkills;
//    @Column(name = "aiRecommendation")
//    private  String
    @ManyToOne
    @JoinColumn(name = "resume_id", referencedColumnName = "id")
    @ToString.Exclude // ✅ prevents stack overflow
    @JsonBackReference("resume-analysis")
    private Resume resume;
    @Column(name="interviewDate")
    private String interviewDate;
    @Column(name="interviewTime")
    private String interviewTime;
   @Column(name="interviewMode")
    private  String interviewMode;
   @Column(name = "selectedStatus")
    private String selectedStatus;

}
