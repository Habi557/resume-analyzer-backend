package com.resume.backend.serviceImplementation;

import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.ResumeDeletionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
public class ResumeDeletionImpl implements ResumeDeletionService {
    private  final ResumeRepository resumeRepository;
    ResumeDeletionImpl(ResumeRepository resumeRepository){
        this.resumeRepository=resumeRepository;
    }
    @Override
    @CacheEvict(value = "resumeCache", allEntries = true)
    public String deleteResume(Long resumeId) {
        resumeRepository.deleteById(resumeId);
        return "Resume Deleted Sucessfully";
    }
}
