package com.resume.backend.batch;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.ResumeResult;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.helperclass.ConvertingEntityToDtos;
import com.resume.backend.helperclass.ResumeAnalysisHelper;
import com.resume.backend.repository.ResumeAnalysisJobRepository;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.serviceImplementation.ResumeAsyncAnalysis;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@StepScope
public class ResumeProcessor implements ItemProcessor<Resume, ResumeAnalysisEntity> {
    private final ConvertingEntityToDtos convertingEntityToDtos;
    //private final ResumeAsyncAnalysis resumeAsyncAnalysis;
    private final ResumeAnalysisHelper resumeAnalysisHelper;
    private final ModelMapper modelMapper;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisJobRepository resumeAnalysisJobRepository;
    @Value("#{jobParameters['jobId']}") String jobId;
    int count = 0;




    public ResumeProcessor(ConvertingEntityToDtos convertingEntityToDtos, ResumeAnalysisHelper resumeAnalysisHelper,ModelMapper modelMapper,ResumeRepository resumeRepository,ResumeAnalysisJobRepository resumeAnalysisJobRepository) {
        this.convertingEntityToDtos = convertingEntityToDtos;
       // this.resumeAsyncAnalysis = resumeAsyncAnalysis;
        this.resumeAnalysisHelper=resumeAnalysisHelper;
        this.modelMapper=modelMapper;
        this.resumeRepository=resumeRepository;
        this.resumeAnalysisJobRepository=resumeAnalysisJobRepository;
    }

    @Override
    public ResumeAnalysisEntity process(Resume resume)  {
        log.info("Processing resume for resume: {}",resume.getId());
        Resume resumeWithSkillsAndEducation = resumeRepository.findById(resume.getId()).orElseThrow(() -> new RuntimeException("Resume not found with id: " + resume.getId()));
        ResumeTempDto resumeTempDto = convertingEntityToDtos.convertResumeDto(resumeWithSkillsAndEducation);
        String jobRole = resumeAnalysisJobRepository.findByJobId(jobId).get().getJobRole();
        ResumeResult resumeResult = resumeAnalysisHelper.safeAnalyze(resumeTempDto, jobRole, jobId);
            if(!resumeResult.success()) {
                log.info("count {}", count);
                count++;
                //if(count==1){
                log.info("Resume analysis failed for resume id {}" ,resume.getId());

                throw new RuntimeException(resumeResult.errorMsg());

                //}
            }
                ResumeAnalysisDTO dto = resumeResult.dto();
                ResumeAnalysisEntity entity = modelMapper.map(dto, ResumeAnalysisEntity.class);
                entity.setId(null);
                entity.setAnalysizedTime(LocalDateTime.now());
               // resume.setScanAllresumesIsChecked(true);
                entity.setResume(resume);
                count++;
                return entity;
    }
}
