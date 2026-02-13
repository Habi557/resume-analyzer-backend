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
import java.util.ArrayList;
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
    @Column(name = "resume_hash", unique = true)
    private String resumeHash;
    //@Column(name="user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @JsonProperty("name")
    @Column(name = "name")
    private String  name;
//    @Lob
//    @Column(name = "skills",columnDefinition = "LONGTEXT")
//    @Convert(converter = StringListConverter.class)
//    private List<String> extractedSkills;
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Skill> skills;
    @Column(name="yearsOfExperience")
    private double yearsOfExperience;
    @Column(name = "fileName")
    private String originalFileName;
    @Column(name = "filePath")
    private String filePath;
    @Column(name = "address")
    private  String address;
    @Lob
    @Column(name = "extracted_text", columnDefinition = "LONGTEXT")
    private String extractedText;
    @Column(name = "uploadTime")
    private LocalDateTime uploadTime;
    @Column(name = "scan_all_resumes")
    private Boolean scanAllresumesIsChecked = Boolean.FALSE;
    @Column(name= "email")
    private String email;
    @Column(name="phone")
    private String phone;
//    @Lob
//    @Convert(converter = StringListConverter.class)
//    @Column(name = "education",columnDefinition = "LONGTEXT")
//    private  List<String> education;
    @Lob
    @Column(name = "redFlags",columnDefinition = "LONGTEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> redFlags = new ArrayList();
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
   // @ToString.Exclude // ✅ prevents stack overflow
    private List<ResumeAnalysisEntity> resumeAnalysisList;
    @Enumerated(EnumType.STRING)
    private ResumeStatus status;
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EducationEntity> educationList = new ArrayList();;
    public void addEducation(EducationEntity edu) {
        if (educationList == null) {
            educationList = new ArrayList<>();
        }
        educationList.add(edu);
        edu.setResume(this);
    }





}
