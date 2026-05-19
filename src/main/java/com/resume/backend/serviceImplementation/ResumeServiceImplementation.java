package com.resume.backend.serviceImplementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.StrategyFactory.ResumeExtractionStrategyFactory;
import com.resume.backend.configurations.AiConfig;
import com.resume.backend.dtos.*;
import com.resume.backend.entity.*;
import com.resume.backend.exceptions.AiNotRespondingException;
import com.resume.backend.exceptions.FileNotFoundEx;
import com.resume.backend.exceptions.InvaidFileFormatException;
import com.resume.backend.exceptions.JsonProcessingRuntimeException;
import com.resume.backend.helperclass.AiApis;
import com.resume.backend.helperclass.ConvertingEntityToDtos;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.helperclass.ResumeSection;
import com.resume.backend.projection.DashboardProjection;
import com.resume.backend.projection.ResumeProjection;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.repository.UserRepository;
import com.resume.backend.services.ResumeParser;
import com.resume.backend.services.ResumeService;
import com.resume.backend.services.StorageService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.codec.digest.DigestUtils;
import org.modelmapper.ModelMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;



@Service
public class ResumeServiceImplementation implements ResumeService {
   // public final ChatClient chatClient;
    private AiApis aiApis;
   // @Value("${upload.dir}")
   // String uploadDir;
    ResumeRepository resumeRepository;
    ResumeHelper resumeHelper;
    ModelMapper modelMapper;
    ResumeAnalysis resumeAnalysis;
    UserRepository userRepository;
    ConvertingEntityToDtos convertingEntityToDtos;
    private StorageService storageService;
    private ResumeExtractionStrategyFactory resumeExtractionStrategyFactory;
//    create a constructor

    public ResumeServiceImplementation(AiApis aiApis, ResumeRepository resumeRepository, ResumeHelper resumeHelper, ResumeAnalysis resumeAnalysis, ModelMapper modelMapper, UserRepository userRepository, ConvertingEntityToDtos convertingEntityToDtos, StorageService storageService, ResumeExtractionStrategyFactory resumeExtractionStrategyFactory) {
        this.aiApis = aiApis;
        this.resumeRepository=resumeRepository;
        this.resumeHelper=resumeHelper;
        this.resumeAnalysis=resumeAnalysis;
        this.modelMapper=modelMapper;
        this.userRepository=userRepository;
        this.convertingEntityToDtos=convertingEntityToDtos;
        this.storageService=storageService;
        this.resumeExtractionStrategyFactory = resumeExtractionStrategyFactory;
    }
    @Caching(evict = {
            @CacheEvict(value = "getAllDashboardDetails", allEntries = true),
            @CacheEvict(value = "getAllResumes", allEntries = true),
            @CacheEvict(value = "getAllAnalysiedResumesDto", allEntries = true)
    })
    @Transactional(rollbackFor = {Exception.class})
    @Override
    public Resume uploadResume(String username, MultipartFile file)  {

            try {
                String originalFilename = file.getOriginalFilename().toLowerCase();

                // Save file (local OR S3 depending on environment)
                String savedFilePathOrKey = storageService.saveFile(file, username);
                // Save the basic information immediately without waiting for Ai processing
               UserEntity user =userRepository.findByUserNameCaseSensitive(username);
               if(user==null){
                   throw new EntityNotFoundException("User not found");
               }
                Resume resume2 = new Resume();
                resume2.setOriginalFileName(originalFilename);
                resume2.setFilePath(savedFilePathOrKey);
                resume2.setUploadTime(LocalDateTime.now());
                resume2.setUser(user);
                resume2.setStatus(ResumeStatus.PROCESSING);
                resumeRepository.save(resume2);
                // Trigger async AI processing
                processResumeAsync(resume2.getId(),file);
                return resume2;

            } catch (IOException e) {
                throw new RuntimeException("Error occurred while saving or reading file", e);
            }

    }

    @Override
    public FileDownloadDataDto dowloadResume(long resumeId) {
        System.out.println("DB HIT for resumeId = " + resumeId);
        return storageService.downloadResume(resumeId);
    }

    public ResumeAnalysisDTO analyzeSingleResumeAsync(ResumeTempDto resume, String jobRole) {
        try {

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

            String template = resumeHelper.loadPromptTemplate2("prompts/resumeScreeningMatcher.txt");
            String prompt = resumeHelper.putValuesToPrompt(
                    template,
                    Map.of("resumeText", tempResumeText, "jobRole", jobRole)
            );

            //  AI call (1 per resume)
            String aiResponse = aiApis.callAiService(prompt);

            String validJson = resumeHelper.extractJson(aiResponse);
            System.out.println("validJson ++++++++++++++++++++++++++++++");
            System.out.println(validJson);

            ResumeAnalysisDTO dto = new ObjectMapper().readValue(validJson, ResumeAnalysisDTO.class);
            dto.setResume(resume);
            System.out.println("dto ++++++++++++++++++++++++++++++");
            System.out.println(dto);
            return dto;

        } catch (Exception e) {
            throw new RuntimeException("AI processing failed", e);
        }
    }
    //@Async
    public void processResumeAsync(Long resumeId,MultipartFile file) {
        ObjectMapper objectMapper = new ObjectMapper();
        Resume resume = resumeRepository.findById(resumeId).orElseThrow();
        try (InputStream fileStream = storageService.loadFile(resume.getFilePath())) {

            // Extract text
            String extractedText;
//            if (resume.getOriginalFileName().toLowerCase().endsWith(".pdf")) {
//                extractedText = resumeHelper.extractTextFromPdf(fileStream);
//            } else {
//                extractedText = resumeHelper.extractTextFromDocx(fileStream);
//            }
            ResumeParser parser = resumeExtractionStrategyFactory.getStrategy(resume.getOriginalFileName());
            extractedText = parser.extractText(fileStream);

            Map<ResumeSection, String> resumeSectionStringMap = resumeHelper.detectSections(extractedText);
            ResumeJson resumeJson = resumeHelper.buildResumeJson(extractedText, resumeSectionStringMap);
            EnumSet<Field> missingFields = resumeHelper.detectMissingFields(resumeJson);
            if(!missingFields.isEmpty()){
                // Build AI prompt
                String prompt = resumeHelper.buildPromptFromEnum(missingFields, extractedText);
                //  Call AI
                 String aiOutput = aiApis.callAiService(prompt);
                 String validJson = resumeHelper.extractJson(aiOutput);
                Map<String, Object> aiMap = objectMapper.readValue(validJson, Map.class);
                resumeJson = resumeHelper.mergeAiUsingEnum(resumeJson, aiMap, missingFields);
                 String redFlagPrompt = resumeHelper.buildPromptForFlags(extractedText);
                String redflags = aiApis.callAiService(redFlagPrompt);
                String jsonString = resumeHelper.extractJson(redflags);
                JsonNode rootNode = objectMapper.readTree(jsonString);

                resumeJson.setRedFlags(
                        rootNode.has("redflags")
                                ? objectMapper.convertValue(rootNode.get("redflags"),
                                new TypeReference<List<String>>() {})
                                : Collections.emptyList()
                );



            }
            // Update resume
            resume.setExtractedText(extractedText);
            // EDUCATION
            if (resumeJson.getEducation() != null) {
                resumeJson.getEducation().stream()
                        .map(e -> modelMapper.map(e, EducationEntity.class))
                        .forEach(resume::addEducation);
            }

            // HEADER
            if (resumeJson.getHeader() != null) {
                resume.setName(resumeJson.getHeader().getName());
                resume.setAddress(resumeJson.getHeader().getCity());
                resume.setEmail(resumeJson.getHeader().getEmail());
                resume.setPhone(resumeJson.getHeader().getPhone());
                resume.setYearsOfExperience(resumeJson.getHeader().getYearsOfExperience());
            }

            // SKILLS
            if (resumeJson.getSkills() != null) {
                List<Skill> listOfSkills = resumeJson.getSkills().stream()
                        .map(skillName -> {
                            Skill skill = new Skill();
                            skill.setName(skillName);
                            skill.setResume(resume);
                            return skill;
                        })
                        .collect(Collectors.toList());
                resume.setSkills(listOfSkills);
            }
            resume.setRedFlags(resumeJson.getRedFlags());
//            String resumeHash = DigestUtils.sha256Hex(file.getInputStream());
//            resume.setResumeHash(resumeHash);

            resume.setStatus(ResumeStatus.UPLOADED);
            resumeRepository.save(resume);

        }catch (NullPointerException e){
            throw new RuntimeException("Null pointerexception occured");
        }
        catch (RestClientException e){
            throw new AiNotRespondingException("Ai service is down try again later");
        }
        catch (Exception e) {
            resume.setStatus(ResumeStatus.FAILED);
            throw new RuntimeException("Exception occured");
        }
    }



}
