package com.resume.backend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.resume.backend.entity.Resume;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResumeAnalysisDTO {
//    private  long totalResumes;
//    private  long canditateScanned;
//    private  int bestMatch;
//    private  double averageExperience;

    @JsonProperty("id")
    private long id;
    @JsonProperty("matchPercentage")
    private int matchPercentage;

    @JsonProperty("extractedSkills")
    private List<String> extractedSkills;
    @JsonProperty("name")
    private String name;
    @JsonProperty("address")
    private  String address;
    @JsonProperty("yearsOfExperience")
    private double yearsOfExperience;

    @JsonProperty("suggestions")
    private List<String> suggestions;

    @JsonProperty("analysis")
    private String conclusion;
    @JsonProperty("analysizedTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime analysizedTime;
    @JsonProperty("topMatchingSkills")
    private List<String> topMatchingSkills;
    @JsonProperty("education")
    private  List<String> education;
    @JsonProperty("resume_id")
    private Resume resume;
    @JsonProperty("email")
    private String email;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("redFlags")
    private List<String> redFlags;
    @JsonProperty("interviewDate")
    private String interviewDate;
    @JsonProperty("interviewTime")
    private String interviewTime;
    @JsonProperty("interviewMode")
    private  String interviewMode;
    @JsonProperty("selectedStatus")
    private String selectedStatus;




//
//    public long getTotalResumes() {
//  return totalResumes;
// }
//
// public void setTotalResumes(long totalResumes) {
//  this.totalResumes = totalResumes;
// }
//
// public long getCanditateScanned() {
//  return canditateScanned;
// }
//
// public void setCanditateScanned(long canditateScanned) {
//  this.canditateScanned = canditateScanned;
// }
//
// public int getBestMatch() {
//  return bestMatch;
// }
//
// public void setBestMatch(int bestMatch) {
//  this.bestMatch = bestMatch;
// }
//
// public double getAverageExperience() {
//  return averageExperience;
// }
//
// public void setAverageExperience(double averageExperience) {
//  this.averageExperience = averageExperience;
// }

 public int getMatchPercentage() {
  return matchPercentage;
 }

 public void setMatchPercentage(int matchPercentage) {
  this.matchPercentage = matchPercentage;
 }

 public List<String> getExtractedSkills() {
  return extractedSkills;
 }

 public void setExtractedSkills(List<String> extractedSkills) {
  this.extractedSkills = extractedSkills;
 }

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public String getAddress() {
  return address;
 }

 public void setAddress(String address) {
  this.address = address;
 }

 public double getYearsOfExperience() {
  return yearsOfExperience;
 }

 public void setYearsOfExperience(double yearsOfExperience) {
  this.yearsOfExperience = yearsOfExperience;
 }

 public List<String> getSuggestions() {
  return suggestions;
 }

 public void setSuggestions(List<String> suggestions) {
  this.suggestions = suggestions;
 }

 public String getConclusion() {
  return conclusion;
 }

 public void setConclusion(String conclusion) {
  this.conclusion = conclusion;
 }

    public LocalDateTime getAnalysizedTime() {
        return analysizedTime;
    }

    public void setAnalysizedTime(LocalDateTime analysizedTime) {
        this.analysizedTime = analysizedTime;
    }
}
