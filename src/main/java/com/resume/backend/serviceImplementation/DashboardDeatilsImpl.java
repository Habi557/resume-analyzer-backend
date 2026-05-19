package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.projection.DashboardProjection;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.DashboardDeatils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DashboardDeatilsImpl implements DashboardDeatils {
    private final ResumeRepository resumeRepository;
    DashboardDeatilsImpl(ResumeRepository resumeRepository){
        this.resumeRepository=resumeRepository;
    }
    @Cacheable(value = "getAllDashboardDetails")
    @Override
    public DashboardDto getAllDashboardDetails() {
//        List<Resume> listOfResumes = resumeRepository.findAll();
//        int totalResumes = listOfResumes.size();
//        long totalResumesAnalysisedCount = listOfResumes.stream().filter(Resume::getScanAllresumesIsChecked).count();
//        int totalResumesNotAnalysisedCount= (int) (totalResumes-totalResumesAnalysisedCount);
//        int totalResumeIncresedPercentage =  (int)(((double) (totalResumesAnalysisedCount - totalResumesNotAnalysisedCount) /  totalResumesAnalysisedCount) * 100);
//        Optional<ResumeAnalysisEntity> maxMatchPercentage = resumeAnalysis.findAll().stream().max(Comparator.comparing(ResumeAnalysisEntity::getMatchPercentage));
//        int candidatesScreened = resumeAnalysis.findAll().size();
//        double totalExperience = 0;
//        int validCount = 0;
//
//        for (Resume resume : listOfResumes) {
//            if (resume.getYearsOfExperience() != 0.0) {
//                totalExperience += resume.getYearsOfExperience();
//                validCount++;
//            }
//        }
//        double averageExperience = Math.round(validCount > 0 ? (totalExperience / validCount) : 0);
//        DashboardDto dashboardDto = new DashboardDto();
//        dashboardDto.setTotalResumes(totalResumes);
//        dashboardDto.setCanditateScanned(candidatesScreened);
//        //dashboardDto.setTotalResumePercentage(totalResumeIncresedPercentage);
//        dashboardDto.setAverageExperience(averageExperience);
//        maxMatchPercentage.ifPresent(resumeAnalysisEntity -> {
//            dashboardDto.setBestMatch(resumeAnalysisEntity.getMatchPercentage());
//           // dashboardDto.setResumeAnalysisEntity(resumeAnalysisEntity);
//            dashboardDto.setResumeAnalysisDTO(convertingEntityToDtos.convertResumeAnalysisEntityToResumeAnalysisDTO(resumeAnalysisEntity));
//        });
//        return dashboardDto;
        DashboardProjection result =resumeRepository.getDashboardDetails();

        DashboardDto dto = new DashboardDto();
        if (result != null) {
            dto.setTotalResumes(safeInt(result.getTotalResumes()));
            dto.setCanditateScanned(safeInt(result.getCandidatesScreened()));
            dto.setAverageExperience(result.getAverageExperience());
            dto.setBestMatch(safeInt(result.getBestMatch()));
        }
        return dto;
    }
    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }
}
