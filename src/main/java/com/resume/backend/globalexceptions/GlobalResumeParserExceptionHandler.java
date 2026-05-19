package com.resume.backend.globalexceptions;

import com.resume.backend.exceptions.ResumeParsingException;
import com.resume.backend.helperclass.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalResumeParserExceptionHandler {
    @ExceptionHandler(ResumeParsingException.class)
    public ResponseEntity<ApiResponse> handleResumeParsingException(ResumeParsingException ex) {
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(ApiResponse.builder().status(400).message(ex.getMessage()).build());
       }
}
