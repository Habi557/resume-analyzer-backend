package com.resume.backend.helperclass;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.ResumeJson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmbeddingHelper {
    private final ObjectMapper objectMapper;

    public String createEmbeddingText(ResumeJson resume) {

        StringBuilder text = new StringBuilder();

        if (resume.getHeader() != null) {
            text.append("Header: ")
                    .append(resume.getHeader())
                    .append("\n");
        }

        if (resume.getExperience() != null) {
            text.append("Experience: ")
                    .append(resume.getExperience())
                    .append("\n");
        }

        if (resume.getEducation() != null) {
            text.append("Education: ")
                    .append(resume.getEducation())
                    .append("\n");
        }

        if (resume.getProjects() != null) {
            text.append("Projects: ")
                    .append(resume.getProjects())
                    .append("\n");
        }

        if (resume.getSkills() != null) {
            text.append("Skills: ")
                    .append(String.join(", ", resume.getSkills()))
                    .append("\n");
        }

        if (resume.getCertifications() != null) {
            text.append("Certifications: ")
                    .append(String.join(", ", resume.getCertifications()))
                    .append("\n");
        }

        return text.toString();
    }
    public String toPgVector(float[] embedding) {

        StringBuilder vector = new StringBuilder("[");

        for (int i = 0; i < embedding.length; i++) {

            if (i > 0) {
                vector.append(",");
            }

            vector.append(embedding[i]);
        }

        vector.append("]");

        return vector.toString();
    }
    public String toJson(Object obj){
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
    public ResumeEmbeddingData convertToResumeEmbeddingData(Long resumeId, ResumeJson resumeJson) {
        return new ResumeEmbeddingData(
                resumeId,
                toJson(resumeJson.getHeader()),
                toJson(resumeJson.getExperience()),
                toJson(resumeJson.getEducation()),
                toJson(resumeJson.getProjects()),
                toJson(resumeJson.getSkills()),
                toJson(resumeJson.getCertifications())
        );
    }
}
