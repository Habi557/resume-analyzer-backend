package com.resume.backend.serviceImplementation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.exceptions.InvaidFileFormatException;
import com.resume.backend.exceptions.JsonProcessingRuntimeException;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.ResumeService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ResumeServiceImplementation implements ResumeService {
    public final ChatClient chatClient;
    @Value("${upload.dir}")
    String uploadDir;
    ResumeRepository resumeRepository;
    ResumeHelper resumeHelper;
    ModelMapper modelMapper;
    ResumeAnalysis resumeAnalysis;
//    create a constructor

    public ResumeServiceImplementation(ChatClient chatClient,ResumeRepository resumeRepository, ResumeHelper resumeHelper, ResumeAnalysis resumeAnalysis, ModelMapper modelMapper) {
        this.chatClient = chatClient;
        this.resumeRepository=resumeRepository;
        this.resumeHelper=resumeHelper;
        this.resumeAnalysis=resumeAnalysis;
        this.modelMapper=modelMapper;

    }

    @Override
    public String generateResumeResponse(String userResumeDeatils) {
       // String response = chatClient.prompt().call().content();

        return "test";
    }

    @Override
    public Resume uploadResume(Long userId, MultipartFile file) throws IOException {
        // Ensure the upload directory exists
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs(); // creates the full path if it doesn't exist
        }

        // Generate unique file path
        String originalFilename = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID() + "_" + originalFilename;
        String filePath = uploadDir + "/" + uniqueFileName;

        // Save the file to disk
        File savedFile = new File(filePath);
        file.transferTo(savedFile);

        // Extract text using Apache PDFBox
       // String extractedText = resumeHelper.extractTextFromPdf(savedFile);
        String extractedText;
        if (file.getOriginalFilename().endsWith(".pdf")) {
            extractedText = resumeHelper.extractTextFromPdf(savedFile);
        } else if (file.getOriginalFilename().endsWith(".docx")) {
            extractedText = resumeHelper.extractTextFromDocx(file);
        } else {
            throw new InvaidFileFormatException("Unsupported File Format");
        }
        System.out.println("//////////////////////////////////");
        // Extract Josn from resumText using AI Model
        String template = resumeHelper.loadPromptTemplate("prompts/extractedResumeTextToJson.txt");
        String finalResumeTextPrompt = resumeHelper.putValuesToPrompt(template, Map.of("resumeText", extractedText));
        String aiOutputJson = this.chatClient.prompt(finalResumeTextPrompt).call().chatResponse().getResult().getOutput().getContent();
        String validJsonString = resumeHelper.extractJson(aiOutputJson);
        ResumeAnalysisDTO resumeAnalysisDTO = new ObjectMapper().readValue(validJsonString, ResumeAnalysisDTO.class);
        // Save resume details in DB
        Resume resume = new Resume();
        resume.setUserId(userId);
        resume.setOriginalFileName(originalFilename);
        resume.setFilePath(filePath);
        resume.setExtractedText(extractedText);
        resume.setUploadTime(LocalDateTime.now());
        resume.setName(resumeAnalysisDTO.getName());
        resume.setExtractedSkills(resumeAnalysisDTO.getExtractedSkills());
        resume.setYearsOfExperience(resumeAnalysisDTO.getYearsOfExperience());
        resume.setAddress(resumeAnalysisDTO.getAddress());
        resume.setEmail(resumeAnalysisDTO.getEmail());
        resume.setPhone(resumeAnalysisDTO.getPhone());
        resume.setRedFlags(resumeAnalysisDTO.getRedFlags().stream().limit(3).toList());

        return resumeRepository.save(resume);
    }

    @Override
    @Transactional
    public List<ResumeAnalysisDTO> resumeScreen(String jobRole, boolean scanAllresumesIsChecked){
        //Load all the resume data
        List<Resume> listOfResumes=this.resumeRepository.findAll();
       // List<ResumeAnalysisDTO> resumeAnalysisDTOS = resumeHelper.extractResumeEntityFromResumeTest(listOfResumes);
        List<ResumeAnalysisDTO> listOfResumeAnalysisDtoAfterFilter;
        // If conditions for all the resumes
        if(scanAllresumesIsChecked){
            listOfResumeAnalysisDtoAfterFilter = resumeScreenAI(listOfResumes, jobRole);
        }
        // else condition for newly add resumes
        else {
            List<Resume> listOfResumeAfterFilter = listOfResumes.stream().filter(resume -> !resume.isScanAllresumesIsChecked()).collect(Collectors.toList());
             listOfResumeAnalysisDtoAfterFilter = resumeScreenAI(listOfResumeAfterFilter, jobRole);
        }

        // convert the list of ResumeAnalysisDTO to ResumeAnalysisEntity
        List<ResumeAnalysisEntity> resumeAnalysisEntityList = listOfResumeAnalysisDtoAfterFilter.stream()
                .map((dto) ->{
                   ResumeAnalysisEntity entity= modelMapper.map(dto, ResumeAnalysisEntity.class);
                   entity.setId(null);
                   entity.setAnalysizedTime(LocalDateTime.now());
                   entity.setResume(dto.getResume());
                   // resumeAnalysis.save(entity);

                    return  entity;
                } )
                .collect(Collectors.toList());
        if(resumeAnalysisEntityList.size()>0 && scanAllresumesIsChecked){
            resumeAnalysis.deleteAll();
        }
        resumeAnalysis.saveAll(resumeAnalysisEntityList);

        return listOfResumeAnalysisDtoAfterFilter;



    }

    @Override
    public List<ResumeAnalysisDTO> getAllAnalysiedResumes(int pageNo,int pageSize) {
        int totalResumes = resumeRepository.findAll().size();
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by("matchPercentage").descending());
        Page<ResumeAnalysisEntity> all = resumeAnalysis.findAll(pageRequest);
        List<ResumeAnalysisEntity> resumeAnalysisEntity = all.getContent();
        // int candidatesScreened = resumeAnalysisEntity.size();
        List<ResumeAnalysisDTO> resumeAnalysisDTOS = resumeAnalysisEntity.stream()
                .map(entity -> {
                    ResumeAnalysisDTO analysisDTO = modelMapper.map(entity, ResumeAnalysisDTO.class);
                   // analysisDTO.setTotalResumes(totalResumes);
                  //  analysisDTO.setCanditateScanned(candidatesScreened);
                    Resume resume = analysisDTO.getResume();
                    List<String> listofFlags = resume.getRedFlags().stream().limit(3).toList();
                    resume.setRedFlags(listofFlags);
                    analysisDTO.setResume(resume);
                    return analysisDTO;

                })
                .sorted(Comparator.comparing(ResumeAnalysisDTO::getMatchPercentage).reversed())
                .collect(Collectors.toList());

        return resumeAnalysisDTOS;
    }

    @Override
    public DashboardDto getAllDashboardDetails() {
        List<Resume> listOfResumes = resumeRepository.findAll();
        int totalResumes = listOfResumes.size();
        long totalResumesAnalysisedCount = listOfResumes.stream().filter(Resume::isScanAllresumesIsChecked).count();
        int totalResumesNotAnalysisedCount= (int) (totalResumes-totalResumesAnalysisedCount);
        int totalResumeIncresedPercentage =  (int)(((double) (totalResumesAnalysisedCount - totalResumesNotAnalysisedCount) /  totalResumesAnalysisedCount) * 100);
        Optional<ResumeAnalysisEntity> maxMatchPercentage = resumeAnalysis.findAll().stream().max(Comparator.comparing(ResumeAnalysisEntity::getMatchPercentage));
        int candidatesScreened = resumeAnalysis.findAll().size();
        double totalExperience = 0;
        int validCount = 0;

        for (Resume resume : listOfResumes) {
            if (resume.getYearsOfExperience() != 0.0) {
                totalExperience += resume.getYearsOfExperience();
                validCount++;
            }
        }
        double averageExperience = validCount > 0 ? totalExperience / validCount : 0;
        DashboardDto dashboardDto = new DashboardDto();
        dashboardDto.setTotalResumes(totalResumes);
        dashboardDto.setCanditateScanned(candidatesScreened);
        dashboardDto.setTotalResumePercentage(totalResumeIncresedPercentage);
        dashboardDto.setAverageExperience(averageExperience);
        maxMatchPercentage.ifPresent(resumeAnalysisEntity -> dashboardDto.setBestMatch(resumeAnalysisEntity.getMatchPercentage()));
        dashboardDto.setResumeAnalysisEntity(maxMatchPercentage.get());


        return dashboardDto;
    }



    @Transactional
    public List<ResumeAnalysisDTO> resumeScreenAI(List<Resume> listOfResumes,String jobRole){
       List<ResumeAnalysisDTO> listOfResumeAnalysisDto = listOfResumes.stream()
                .map(resume -> {
                    try {
                        // Step 1: Prepare the resume text with template
                        String tempResumeText="Name: "+resume.getName()+" skills: "+resume.getExtractedSkills()+" Experience: "+resume.getYearsOfExperience()+" address: "+resume.getAddress();
                        String template = resumeHelper.loadPromptTemplate("prompts/resumeScreeningMatcher.txt");
                        String resumeText = resumeHelper.putValuesToPrompt(template, Map.of("resumeText", tempResumeText, "jobRole", jobRole));

                        // Step 2: Get AI response
                        String content = chatClient.prompt()
                                .user(resumeText)
                                .call()
                                .chatResponse()
                                .getResult()
                                .getOutput()
                                .getContent();

                        // Step 3: Process the JSON response
                        String validJson = resumeHelper.extractJson(content);
                        System.out.println("validJson ++++++++++++++++++++++++++++++");
                        System.out.println(validJson);
                        resume.setScanAllresumesIsChecked(true);
                       //resumeRepository.save(resume);

                        ResumeAnalysisDTO resumeAnalysisDTO = new ObjectMapper().readValue(validJson, ResumeAnalysisDTO.class);
                        resumeAnalysisDTO.setResume(resume);
                        return resumeAnalysisDTO;

                    }
//                    catch(JsonProcessingException e){
//                        throw new JsonProcessingRuntimeException("Invalid Json format response",e);
//                    }
                    catch (IOException e) {
                        throw new RuntimeException("Failed to load template or process resume", e);
                    }

                })
                .sorted(Comparator.comparing(ResumeAnalysisDTO::getMatchPercentage))
                .collect(Collectors.toList());
       resumeRepository.saveAll(listOfResumes);
       return listOfResumeAnalysisDto;

    }
    @Override
    public Resource dowloadResume(long resumeId) {
        Optional<Resume> resume = resumeRepository.findById(resumeId);
        Resource resource = null;
        if(resume.isPresent()){
            Path filePath = Paths.get(resume.get().getFilePath());
             resource = new FileSystemResource(filePath);
        }

        return resource;
    }

    @Override
    public List<Resume> getAllResumes() {
         return resumeRepository.findAll();
    }


}
