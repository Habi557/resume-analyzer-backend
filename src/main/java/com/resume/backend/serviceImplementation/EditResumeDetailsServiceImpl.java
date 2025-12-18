package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.EditResumeDeatilsDto;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.EditResumeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
@Service
public class EditResumeDetailsServiceImpl implements EditResumeDetailsService {
    @Autowired
    ResumeRepository resumeRepository;
    @Override
    public String editResumeDetails(EditResumeDeatilsDto resumeTempDto) {
        resumeRepository.findById(resumeTempDto.getId()).ifPresent(resume -> {
            resume.setName(resumeTempDto.getName());
            resume.setEmail(resumeTempDto.getEmail());
//            resume.setPhone(resumeTempDto.getPhone());
//            resume.setAddress(resumeTempDto.getAddress());
           // resume.setSkills(resumeTempDto.getSkills().stream().map(skill -> new Skill(skill)).collect(Collectors.toList()));
           // resume.setEducation(resumeTempDto.getEducation());
            resume.setYearsOfExperience(resumeTempDto.getYearsOfExperience());
           // resume.setRedFlags(resumeTempDto.getRedFlags());
            resumeRepository.save(resume);
        });
        return "updated sucessfully";
    }
}
