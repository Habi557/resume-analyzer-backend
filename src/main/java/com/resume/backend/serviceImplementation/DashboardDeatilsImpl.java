package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.projection.DashboardProjection;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.DashboardDeatils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class DashboardDeatilsImpl implements DashboardDeatils {
    private final ResumeRepository resumeRepository;
    DashboardDeatilsImpl(ResumeRepository resumeRepository){
        this.resumeRepository=resumeRepository;
    }
    @Cacheable(value = "getAllDashboardDetails")
    @Override
    public DashboardDto getAllDashboardDetails() {
        DashboardProjection result =resumeRepository.getDashboardDetails();

        DashboardDto dto = new DashboardDto();
        if (result != null) {
            dto.setTotalResumes(safeInt(result.getTotalResumes()));
            dto.setCanditateScanned(safeInt(result.getCandidatesScreened()));
            dto.setAverageExperience(result.getAverageExperience());
            dto.setBestMatch(safeInt(result.getBestMatch()));
        }
        log.debug("Fetching dashboard details {}",dto);

        return dto;
    }
    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }
}
