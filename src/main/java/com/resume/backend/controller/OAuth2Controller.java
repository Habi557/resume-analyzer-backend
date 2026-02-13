package com.resume.backend.controller;

import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@PreAuthorize("permitAll()")
public class OAuth2Controller {
    @Autowired
    private AuthService authService;
    @GetMapping("/oauth-success")
    public ResponseEntity<AuthResponse> oauthSuccess(HttpServletRequest request) {
        return ResponseEntity.ok(authService.buildAuthResponseFromRefreshToken(request));
    }
}
