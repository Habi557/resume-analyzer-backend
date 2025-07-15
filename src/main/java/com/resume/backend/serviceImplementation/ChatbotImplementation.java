package com.resume.backend.serviceImplementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.ChatBotAnalysis;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.Skill;
import com.resume.backend.exceptions.AiNotRespondingException;
import com.resume.backend.exceptions.TemplateNotFoundException;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.helperclass.ResumeSpecification;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.ChatbotService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotImplementation implements ChatbotService {
    private ChatClient chatClient;
    private ResumeHelper resumeHelper;
    private ResumeRepository resumeRepository;

    public  ChatbotImplementation(ChatClient chatClient, ResumeHelper resumeHelper, ResumeRepository resumeRepository) {
        this.chatClient = chatClient;
        this.resumeHelper=resumeHelper;
        this.resumeRepository=resumeRepository;
    }
    @Override
    public List<Resume> chatbotCall(String query) {
//        try {
//            String template = resumeHelper.loadPromptTemplate("prompts/chatbotAnalysisTextToJson.txt");
//            String prompt = resumeHelper.putValuesToPrompt(template, Map.of("query", query));
//            String content = this.chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getContent();
//            String validJson = this.resumeHelper.extractJson(content);
//            ChatBotAnalysis chatBotAnalysis = new ObjectMapper().readValue(validJson, ChatBotAnalysis.class);
//            List<Resume> byYearsOfExperience=null;
//            if(chatBotAnalysis.getYearsOfExperience()!=0.0){
//                byYearsOfExperience = this.resumeRepository.findByYearsOfExperience(chatBotAnalysis.getYearsOfExperience());
//            }
//            return byYearsOfExperience;
//        } catch (IOException e) {
//            throw new RuntimeException(e.getMessage());
//
//        }
        return null;
    }
    public String getAnswer(String message) {
        try {
            // Step 1: Load and inject prompt
            String template = resumeHelper.loadPromptTemplate("prompts/chatbotAnalysisTextToJson.txt");
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
                return "Please mention a skill or experience to filter the resumes.";
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

            // Step 6: Return chatbot response
            return buildResponse(results, String.join(",", skills != null ? skills : new String[0]), experience);

        } catch (IOException e) {
            throw new RuntimeException("Failed to process chatbot request: " + e.getMessage(), e);
        }
        catch (RestClientException e){

            throw new AiNotRespondingException("Ai server is down restart the server");
        }
    }

//    public String getAnswer(String message) {
//        try {
//            // Step 1: Build the AI prompt and get the JSON response
//            String template = resumeHelper.loadPromptTemplate("prompts/chatbotAnalysisTextToJson.txt");
//            String prompt = resumeHelper.putValuesToPrompt(template, Map.of("query", message));
//            String content = chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getContent();
//            String validJson = resumeHelper.extractJson(content);
//
//            // Step 2: Map response to DTO
//            ChatBotAnalysis chatBotAnalysis = new ObjectMapper().readValue(validJson, ChatBotAnalysis.class);
//            String[] inputSkills = chatBotAnalysis.getSkills();
//            double experience = chatBotAnalysis.getYearsOfExperience();
//            System.out.println("Skills from AI: " + Arrays.toString(inputSkills));
//
//            // Step 3: Define skill alias map
//
//            Map<String, List<String>> skillAliases = Map.ofEntries(
//                    // Java stack
//                    Map.entry("java", List.of("java", "java8", "j2se", "j2ee", "core java", "java developer")),
//                    Map.entry("spring boot", List.of("spring boot", "spring-boot")),
//
//                    // .NET stack
//                    Map.entry(".net", List.of(".net", ".net developer", ".net framework", "dotnet", "asp .net", "asp.net", "aps .net")),
//                    Map.entry(".net core", List.of(".net core", ".net core web api", "dotnet core", "aps .net", "asp .net", "asp.net")),
//                    Map.entry("net core", List.of(".net core", ".net core web api", "dotnet core", "net core")),
//                    Map.entry("web api", List.of("web api", ".net core web api", "rest api", "restful api", "asp.net web api")),
//                    Map.entry("asp.net", List.of("asp.net", "asp net", "asp.net mvc")),
//                    Map.entry("c#", List.of("c#", "c sharp", "csharp")),
//
//                    // Dev tools
//                    Map.entry("postman", List.of("postman", "post man")),
//                    Map.entry("git", List.of("git", "github")),
//                    Map.entry("svn", List.of("svn")),
//                    Map.entry("visual studio code", List.of("visual studio code", "vs code", "vscode")),
//                    Map.entry("ssms", List.of("ssms", "sql server management studio")),
//                    Map.entry("notepad++", List.of("notepad++")),
//                    Map.entry("workbench", List.of("workbench")),
//                    Map.entry("windows", List.of("windows")),
//
//                    // Frontend
//                    Map.entry("angular", List.of("angular", "angular 15")),
//                    Map.entry("typescript", List.of("typescript", "typesript")),
//                    Map.entry("javascript", List.of("javascript", "js")),
//                    Map.entry("html", List.of("html", "html5")),
//                    Map.entry("css", List.of("css", "css3")),
//                    Map.entry("bootstrap", List.of("bootstrap")),
//
//                    // Backend & Data
//                    Map.entry("sql", List.of("sql", "mysql", "sql server")),
//                    Map.entry("mysql", List.of("mysql", "my sql")),
//                    Map.entry("json", List.of("json")),
//                    Map.entry("xml", List.of("xml"))
//            );
//
//
//            // Step 4: Build dynamic query
//            Specification<Resume> spec = Specification.where(null);
//
//            for (String inputSkill : inputSkills) {
//                String key = inputSkill.toLowerCase();
//                List<String> aliases = skillAliases.get(key);
//
//                if (aliases != null && !aliases.isEmpty()) {
//                    spec = spec.and(ResumeSpecification.hasAnyAlias(aliases));
//                } else {
//                    spec = spec.and(ResumeSpecification.hasSkill(inputSkill));
//                }
//            }
//
//            if (experience > 0.0) {
//                spec = spec.and(ResumeSpecification.hasExperienceGreaterThan(experience));
//            }
//
//            // Step 5: Query results
//            List<Resume> results = resumeRepository.findAll(spec);
//
//            // Step 6: Return chatbot response
//            return buildResponse(results, String.join(", ", inputSkills), experience);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to parse AI output: " + e.getMessage(), e);
//        } catch (RestClientException e){
//
//            throw new AiNotRespondingException("Ai server is down restart the server");
//        }
//    }


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
