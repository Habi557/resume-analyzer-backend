package com.resume.backend.services;

import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.dtos.LoginRequest;
import com.resume.backend.dtos.UserDto;
import com.resume.backend.entity.UserEntity;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse refreshToken(String refreshToken);
    void logout(String accessToken);
    UserEntity register(UserDto userDto);
    AuthResponse buildAuthResponseFromRefreshToken(HttpServletRequest request);
}
