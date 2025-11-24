package com.resume.backend.services;

import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.dtos.LoginRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse refreshToken(String refreshToken);
    void logout(String accessToken);
}
