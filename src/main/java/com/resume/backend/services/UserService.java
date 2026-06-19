package com.resume.backend.services;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.UserDto;
import com.resume.backend.entity.UserEntity;

import java.util.List;

public interface UserService {
    List<ResumeAnalysisDTO> getAllUsersAnalysizedResumes(String username);
    UserDto getUser(String username);
}
