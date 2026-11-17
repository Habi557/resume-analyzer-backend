package com.resume.backend.globalexceptions;

import com.resume.backend.exceptions.JobLaunchException;
import com.resume.backend.helperclass.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class JobExceptionHandler {
    @ExceptionHandler(JobLaunchException.class)
    public ResponseEntity<ApiResponse> handleJobLaunchException(JobLaunchException ex) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(ApiResponse.builder().success(false).status(409).message(ex.getMessage()).build());
    }
}
