package com.resume.backend.controller;

import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.dtos.LoginRequest;
import com.resume.backend.services.AuthService;
import com.resume.backend.services.JitsiMeetingService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class LoginContorller {
    @Autowired
    private AuthService authService;
    @Autowired
    JitsiMeetingService jitsiMeetingService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(loginRequest);
        String jetiurl = jitsiMeetingService.generateMeetingLink("venkat", "habi");
        System.out.println("jetiurl "+jetiurl);

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", authResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        response.addHeader("Set-Cookie",refreshCookie.toString());
        authResponse.setRefreshToken(null);

        return  ResponseEntity.ok(authResponse);
    }
    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        System.out.println("refreshToken "+refreshToken);
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        authResponse.setRefreshToken(null);
        return ResponseEntity.ok(authResponse);
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody String accessToken) {
        authService.logout(accessToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

}
