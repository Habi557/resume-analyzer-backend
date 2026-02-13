package com.resume.backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ResumeJson {

    private HeaderJson header;           // name, email, phone, city
    private List<ExperienceJson> experience;
    private List<EducationJson> education;
    private List<ProjectJson> projects;
    private List<String> skills;
    private List<String> certifications;
    @JsonProperty("redflags")
    private List<String> redFlags;

    // getters & setters
}

