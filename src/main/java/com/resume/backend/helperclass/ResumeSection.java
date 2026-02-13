package com.resume.backend.helperclass;

import java.util.List;

public enum ResumeSection {
    SUMMARY(List.of("summary", "profile", "objective")),
    SKILLS(List.of("skills", "technical skills", "tech stack")),
    EXPERIENCE(List.of("experience", "work experience", "employment")),
    EDUCATION(List.of("education", "academics")),
    PROJECTS(List.of("projects")),
    CERTIFICATIONS(List.of("certifications"));

    public final List<String> keywords;
    ResumeSection(List<String> keywords) {
        this.keywords = keywords;
    }
}

