package com.resume.backend.serviceImplementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.ChatBotAnalysis;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.Skill;
import com.resume.backend.exceptions.AiNotRespondingException;
import com.resume.backend.exceptions.TemplateNotFoundException;
import com.resume.backend.helperclass.ConvertingEntityToDtos;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.helperclass.ResumeSpecification;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.ChatbotService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.util.stream.Collectors;

@Service
public class ChatbotImplementation implements ChatbotService {
    private ChatClient chatClient;
    private ResumeHelper resumeHelper;
    private ResumeRepository resumeRepository;
    private ConvertingEntityToDtos convertingEntityToDtos;

    public  ChatbotImplementation(ChatClient chatClient, ResumeHelper resumeHelper, ResumeRepository resumeRepository, ConvertingEntityToDtos convertingEntityToDtos) {
        this.chatClient = chatClient;
        this.resumeHelper=resumeHelper;
        this.resumeRepository=resumeRepository;
        this.convertingEntityToDtos=convertingEntityToDtos;
    }

    public List<ResumeAnalysisDTO> getAnswer(String message) {
        try {
            // Step 1: Load and inject prompt
            String template = resumeHelper.loadPromptTemplate2("prompts/chatbotAnalysisTextToJson.txt");
            String prompt = resumeHelper.putValuesToPrompt(template, Map.of("query", message));

            // Step 2: Call AI and extract JSON
            String content = this.chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getContent();
            String validJson = this.resumeHelper.extractJson(content);

            // Step 3: Parse JSON into DTO
            ChatBotAnalysis chatBotAnalysis = new ObjectMapper().readValue(validJson, ChatBotAnalysis.class);
            String[] skills = chatBotAnalysis.getSkills();
            Double experience = chatBotAnalysis.getYearsOfExperience();
            System.out.println("Skills from AI: " + Arrays.toString(skills));
            if ((skills == null || skills.length == 0) && (experience == null || experience == 0.0)) {
               // return "Please mention a skill or experience to filter the resumes.";
                throw new RuntimeException("Please mention a skill or experience to filter the resumes.");
            }
            // Step 4: Build Specifications
            Specification<Resume> spec = Specification.where(null);

            // Use OR for skill match: (skill LIKE '%angular%' OR skill LIKE '%react%' OR ...)
            if (skills != null && skills.length > 0) {
                Specification<Resume> skillSpec = null;
                for (String skill : skills) {
                    if (skillSpec == null) {
                        skillSpec = ResumeSpecification.hasSkill(skill.trim());
                    } else {
                        skillSpec = skillSpec.or(ResumeSpecification.hasSkill(skill.trim()));
                    }
                }
                if (skillSpec != null) {
                    spec = spec.and(skillSpec);
                }
            }

            // Add experience filter
            if (experience != null && experience > 0) {
                spec = spec.and(ResumeSpecification.hasExperienceGreaterThan(experience));
            }

            // Step 5: Execute search
            List<Resume> results = resumeRepository.findAll(spec);
            List<ResumeAnalysisDTO> collect = results.stream()
                    .map(resume -> resume.getResumeAnalysisList().stream()
                            .max(Comparator.comparing(ResumeAnalysisEntity::getMatchPercentage))
                            .map(convertingEntityToDtos::convertResumeAnalysisEntityToResumeAnalysisDTO)
                            .orElse(null)   // safe
                    )
                    .filter(Objects::nonNull)   // remove resumes without analysis
                    .collect(Collectors.toList());
            return collect;



            // Step 6: Return chatbot response
           // return buildResponse(results, String.join(",", skills != null ? skills : new String[0]), experience);

        } catch (IOException e) {
            throw new RuntimeException("Failed to process chatbot request: " + e.getMessage(), e);
        }
        catch (RestClientException e){

            throw new AiNotRespondingException("Ai server is down restart the server");
        }
    }



    private String buildResponse(List<Resume> resumes, String skillsQuery, Double experience) {
        if (resumes.isEmpty()) {
            return "No matching resumes found for skills: " + skillsQuery + " and experience: " + experience + " years.";
        }

        StringBuilder response = new StringBuilder();
        response.append("Found ").append(resumes.size()).append(" matching resume(s):\n");

        for (Resume resume : resumes) {
            String name = resume.getName();
            Double exp = resume.getYearsOfExperience();

            // Convert List<Skill> to List<String> of skill names
            List<String> skillNames = resume.getSkills()
                    .stream()
                    .map(Skill::getName)
                    .toList();

            response.append("- ")
                    .append(name)
                    .append(" (")
                    .append(exp)
                    .append(" yrs): ")
                   // .append(skillNames)
                    .append("\n");
        }

        return response.toString();
    }

//    private String extractSkillFromText(String message) {
//        // This regex extracts words after "with", "having", or "skills"
//        Pattern pattern = Pattern.compile("(?:with|having|skills?)\\s+([a-zA-Z0-9\\s,]+)");
//        Matcher matcher = pattern.matcher(message.toLowerCase());
//
//        if (matcher.find()) {
//            return matcher.group(1).trim(); // e.g., "java", "python, spring"
//        }
//        return null;
//    }

//    private Double extractExperienceFromText(String message) {
//        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)\\s*\\+?\\s*years?");
//        Matcher matcher = pattern.matcher(message.toLowerCase());
//
//        if (matcher.find()) {
//            return Double.parseDouble(matcher.group(1));
//        }
//        return null;
//    }
public static Specification<Resume> hasAnyAlias(List<String> aliases) {
    return (root, query, cb) -> {
        Join<Object, Object> skillJoin = root.join("skills", JoinType.INNER);
        List<Predicate> predicates = new ArrayList<>();
        for (String alias : aliases) {
            predicates.add(cb.equal(cb.lower(skillJoin.get("name")), alias.toLowerCase()));
        }
        return cb.or(predicates.toArray(new Predicate[0]));
    };
}


}
