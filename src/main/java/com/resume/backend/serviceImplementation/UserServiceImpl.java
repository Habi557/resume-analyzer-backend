package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.helperclass.ConvertingEntityToDtos;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    ResumeAnalysis resumeAnalysis;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    ConvertingEntityToDtos convertingEntityToDtos;
    @Override
    public List<ResumeAnalysisDTO> getAllUsersAnalysizedResumes(String username) {
        List<ResumeAnalysisEntity> allUsersAnalysizedResumes = resumeAnalysis.getAllUsersAnalysizedResumes(username);
        List<ResumeAnalysisDTO> listofResumeAnalysisDTO = allUsersAnalysizedResumes.stream().map(convertingEntityToDtos::convertResumeAnalysisEntityToResumeAnalysisDTO).collect(Collectors.toList());
        //  allUsersAnalysizedResumes.stream().map()
//        List<ResumeAnalysisDTO> resumeAnalysisDTOS = allUsersAnalysizedResumes.stream()
//                .map(entity -> {
//                    ResumeAnalysisDTO analysisDTO = modelMapper.map(entity, ResumeAnalysisDTO.class);
                    // analysisDTO.setTotalResumes(totalResumes);
                    //  analysisDTO.setCanditateScanned(candidatesScreened);
//                    Resume resume = analysisDTO.getResume();
//                    List<String> listofFlags = resume.getRedFlags().stream().limit(3).toList();
//                    resume.setRedFlags(listofFlags);
//                    analysisDTO.setResume(resume);
//                    return analysisDTO;
//
//                })
//                .sorted(Comparator.comparing(ResumeAnalysisDTO::getMatchPercentage).reversed())
//                .collect(Collectors.toList());
        return listofResumeAnalysisDTO;
    }
}
