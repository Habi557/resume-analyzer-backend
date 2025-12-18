package com.resume.backend.helperclass;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.dtos.SkillDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.Skill;
import com.resume.backend.entity.UserEntity;
import org.hibernate.annotations.Comment;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConvertingEntityToDtos {
    @Autowired
    ModelMapper modelMapper;

    public ResumeAnalysisDTO convertResumeAnalysisEntityToResumeAnalysisDTO(ResumeAnalysisEntity resumeAnalysisEntity) {
        ResumeAnalysisDTO resumeAnlysisDto = new ResumeAnalysisDTO();
        resumeAnlysisDto.setId(resumeAnalysisEntity.getId());
        resumeAnlysisDto.setMatchPercentage(resumeAnalysisEntity.getMatchPercentage());
        resumeAnlysisDto.setSuggestions(resumeAnalysisEntity.getSuggestions());
        resumeAnlysisDto.setConclusion(resumeAnalysisEntity.getConclusion());
        resumeAnlysisDto.setAnalysizedTime(resumeAnalysisEntity.getAnalysizedTime());
        resumeAnlysisDto.setTopMatchingSkills(resumeAnalysisEntity.getTopMatchingSkills());
        resumeAnlysisDto.setResume(convertResumeDto(resumeAnalysisEntity.getResume()));
        resumeAnlysisDto.setInterviewDate(resumeAnalysisEntity.getInterviewDate());
        resumeAnlysisDto.setInterviewTime(resumeAnalysisEntity.getInterviewTime());
        resumeAnlysisDto.setSelectedStatus(resumeAnalysisEntity.getSelectedStatus());
        resumeAnlysisDto.setInterviewMode(resumeAnalysisEntity.getInterviewMode());
        return resumeAnlysisDto;
    }
    public  ResumeTempDto convertResumeDto(Resume resume) {
        ResumeTempDto resumeTempDto = new ResumeTempDto();
        resumeTempDto.setId(resume.getId());
        resumeTempDto.setName(resume.getName());
        resumeTempDto.setEmail(resume.getEmail());
        resumeTempDto.setPhone(resume.getPhone());
        resumeTempDto.setAddress(resume.getAddress());
        List<SkillDto> skills = resume.getSkills().stream().map(skill -> SkillDto.builder().id(skill.getId()).name(skill.getName()).build()).toList();
        resumeTempDto.setSkills(skills);
        if(resume.getEducation()!=null){
            resumeTempDto.setEducation(resume.getEducation().toString());
        }
        resumeTempDto.setYearsOfExperience(resume.getYearsOfExperience());
        resumeTempDto.setRedFlags(resume.getRedFlags());
        return resumeTempDto;
    }
    public  Resume convertToResumeEntity(ResumeTempDto resumeTempDto){
        Resume resume = new Resume();
        resume.setId(resumeTempDto.getId());
        UserEntity userEntity = modelMapper.map(resumeTempDto.getUserDto(), UserEntity.class);
        resume.setUser(userEntity);
        resume.setName(resumeTempDto.getName());
        resume.setSkills(resumeTempDto.getSkills().stream().map(this::convertToSkill).toList());
        resume.setYearsOfExperience(resumeTempDto.getYearsOfExperience());
        resume.setAddress(resumeTempDto.getAddress());
       // resume.setEducation(resumeTempDto.getEducation());
        resume.setPhone(resumeTempDto.getPhone());
        resume.setEmail(resumeTempDto.getEmail());
        resume.setRedFlags(resumeTempDto.getRedFlags());
        return null;

    }
    public  Skill convertToSkill(SkillDto skillDto){
        return modelMapper.map(skillDto, Skill.class);
    }

}

