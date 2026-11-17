package com.resume.backend.helperclass;

public record ResumeEmbeddingData(Long resumeId, String headerJson, String experienceJson, String educationJson, String projectsJson, String skillsJson, String certificationsJson) {
}
