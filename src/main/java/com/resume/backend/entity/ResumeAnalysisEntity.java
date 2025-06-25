package com.resume.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.resume.backend.helperclass.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;

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
    @Column(name = "extractedSkills",columnDefinition = "LONGTEXT")
    private List<String> extractedSkills;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private  String address;
    @Column(name = "yearsOfExperience")
    private double yearsOfExperience;
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
    @Lob
    @Convert(converter = StringListConverter.class)
    @Column(name = "education",columnDefinition = "LONGTEXT")
    private  List<String> education;
    @ManyToOne
    @JoinColumn(name = "resume_id", referencedColumnName = "id")
    private Resume resume;

}
