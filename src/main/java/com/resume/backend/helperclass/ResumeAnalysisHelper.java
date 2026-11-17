package com.resume.backend.helperclass;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.ResumeResult;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.dtos.SkillDto;
import com.resume.backend.exceptions.AiNotRespondingException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ResumeAnalysisHelper {
    private final ResumeHelper resumeHelper;
    private final AiApis aiApis;
    private final ObjectMapper objectMapper;
    private String template;
    @PostConstruct
    public void init() {
        template = resumeHelper.loadPromptTemplate2("prompts/resumeScreeningMatcher.txt");

    }

    public ResumeAnalysisHelper(ResumeHelper resumeHelper, AiApis aiApis, ObjectMapper objectMapper) {
        this.resumeHelper = resumeHelper;
        this.aiApis = aiApis;
        this.objectMapper = objectMapper;
    }

    public ResumeAnalysisDTO analyzeSingleResumeAsync(ResumeTempDto resume, String jobRole) {
            String skills = resume.getSkills()
                    .stream()
                    .map(SkillDto::getName)
                    .collect(Collectors.joining(", "));

            String tempResumeText = """
                    Name: %s
                    Skills: %s
                    Experience: %s years
                    Address: %s
                    """.formatted(
                    resume.getName(),
                    skills,
                    resume.getYearsOfExperience(),
                    resume.getAddress()
            );

            //String template = resumeHelper.loadPromptTemplate2("prompts/resumeScreeningMatcher.txt");
            String prompt = resumeHelper.putValuesToPrompt(
                    template,
                    Map.of("resumeText", tempResumeText, "jobRole", jobRole)
            );

            String aiResponse = aiApis.callAiService(prompt);
            String validJson = resumeHelper.extractJson(aiResponse);

            // log.debug("AI response JSON: {}", validJson);

        ResumeAnalysisDTO dto = null;
        try {
            dto = objectMapper.readValue(validJson, ResumeAnalysisDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        dto.setResume(resume);

            //log.debug("Parsed DTO: {}", dto);
            return dto;

    }

    public ResumeResult safeAnalyze(ResumeTempDto resume, String jobRole, String jobId) {
        try {
            return ResumeResult.ok(analyzeSingleResumeAsync(resume, jobRole));
        } catch (Exception e) {
            // ✅ Unwrap — RestClientException may be buried inside RuntimeException
            Throwable cause = e.getCause() != null ? e.getCause() : e;

            if (cause instanceof RestClientException) {
                log.warn("AI service down for resumeId={}: {}", resume.getId(), cause.getMessage());
                return ResumeResult.fail("AI service is down, try again later");
            }
            log.warn("Failed to analyze resumeId={} name={}: failed reason{}",
                    resume.getId(), resume.getName(), e.getMessage());


            return ResumeResult.fail(e.getMessage());
        }
    }

}
