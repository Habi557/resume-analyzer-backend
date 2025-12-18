package com.resume.backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.resume.backend.helperclass.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Getter
@Setter
@Entity
@Table(name="resume")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //@Column(name="user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @JsonProperty("name")
    @Column(name = "canditate_name")
    private String  name;
//    @Lob
//    @Column(name = "skills",columnDefinition = "LONGTEXT")
//    @Convert(converter = StringListConverter.class)
//    private List<String> extractedSkills;
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    @JsonManagedReference("resume-skill")
    @JsonProperty("skills")
    private List<Skill> skills;
    @Column(name="yearsOfExperience")
    @JsonProperty("yearsOfExperience")
    private double yearsOfExperience;
    @Column(name = "fileName")
    private String originalFileName;
    @Column(name = "filePath")
    private String filePath;
    @Column(name = "canditate_address")
    @JsonProperty("address")
    private  String address;
    @Lob
    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText;
    @Column(name = "uploadTime")
    private LocalDateTime uploadTime;
    @Column(name = "scanAllresumesIsChecked")
    private Boolean scanAllresumesIsChecked = Boolean.FALSE;
    @Column(name= "email")
    @JsonProperty("email")
    private String email;
    @Column(name="phone")
    @JsonProperty("phone")
    private String phone;
    @Lob
    @Convert(converter = StringListConverter.class)
    @Column(name = "education",columnDefinition = "LONGTEXT")
    @JsonProperty("education")
    private  List<String> education;
    @Lob
    @Column(name = "redFlags",columnDefinition = "LONGTEXT")
    @JsonProperty("redFlags")
    @Convert(converter = StringListConverter.class)
    private List<String> redFlags;
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // ✅ prevents stack overflow
    @JsonManagedReference("resume-analysis")
    private List<ResumeAnalysisEntity> resumeAnalysisList;
    @Column(name="status")
    private String status;




}
