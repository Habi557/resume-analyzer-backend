package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.EditResumeDeatilsDto;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.EditResumeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;
@Service
public class EditResumeDetailsServiceImpl implements EditResumeDetailsService {
    ResumeRepository resumeRepository;
    EditResumeDetailsServiceImpl (ResumeRepository resumeRepository){
        this.resumeRepository = resumeRepository;
    }
    @Transactional
    @Override
    public String editResumeDetails(EditResumeDeatilsDto resumeTempDto) {
        Resume resume = resumeRepository.findById(resumeTempDto.getId()).orElseThrow(() -> new RuntimeException("Resume Not Found with Id " + resumeTempDto.getId()));
        resume.setName(resumeTempDto.getName());
        resume.setEmail(resumeTempDto.getEmail());
        resume.setYearsOfExperience(resumeTempDto.getYearsOfExperience());


        return "updated sucessfully";
    }
}
