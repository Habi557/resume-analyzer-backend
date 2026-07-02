package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.UserDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.helperclass.ConvertingEntityToDtos;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.UserRepository;
import com.resume.backend.services.UserService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    ResumeAnalysis resumeAnalysis;
    ModelMapper modelMapper;
    ConvertingEntityToDtos convertingEntityToDtos;
    private final UserRepository userRepository;
    @Override
    public List<ResumeAnalysisDTO> getAllUsersAnalysizedResumes(String username) {
        List<ResumeAnalysisEntity> allUsersAnalysizedResumes = resumeAnalysis.getAllUsersAnalysizedResumes(username).orElseThrow(() -> new RuntimeException("No resumes found for user"));
        List<ResumeAnalysisDTO> listofResumeAnalysisDTO = allUsersAnalysizedResumes.stream().map(convertingEntityToDtos::convertResumeAnalysisEntityToResumeAnalysisDTO).collect(Collectors.toList());
        return listofResumeAnalysisDTO;
    }

    @Override
    public UserDto getUser(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        modelMapper.typeMap(UserEntity.class, UserDto.class).addMappings(mapper -> mapper.skip(UserDto::setPassword));
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        return userDto;
    }
}
