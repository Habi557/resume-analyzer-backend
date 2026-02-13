package com.resume.backend.globalexceptions;

import com.resume.backend.dtos.AuthResponse;
import com.resume.backend.exceptions.TokenExpiredException;
import com.resume.backend.helperclass.ApiResponse;
import com.resume.backend.helperclass.ProblemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @Autowired
    ProblemFactory problemFactory;
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse> handleTokenExpiredException(TokenExpiredException ex) {
       // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(ApiResponse.builder().status(401).message("Token Expired").build());

    }
}
