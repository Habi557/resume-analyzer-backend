package com.resume.backend.entity;

import com.resume.backend.helperclass.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
@Entity
@Table(name="resume")
public class Resume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="user_id")
    private Long userId;
    @Column(name = "canditate_name")
    private String  name;
    @Lob
    @Column(name = "skills",columnDefinition = "LONGTEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> extractedSkills;
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
    @Column(name = "scanAllresumesIsChecked")
    private Boolean scanAllresumesIsChecked = Boolean.FALSE;
    @Column(name= "email")
    private String email;
    @Column(name="phone")
    private String phone;
    @Lob
    @Column(name = "redFlags",columnDefinition = "LONGTEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> redFlags;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getExtractedSkills() {
        return extractedSkills;
    }

    public void setExtractedSkills(List<String> extractedSkills) {
        this.extractedSkills = extractedSkills;
    }

    public double getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(double yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Boolean isScanAllresumesIsChecked() {
        return scanAllresumesIsChecked;
    }

    public void setScanAllresumesIsChecked(Boolean scanAllresumesIsChecked) {
        this.scanAllresumesIsChecked = scanAllresumesIsChecked;
    }
}
