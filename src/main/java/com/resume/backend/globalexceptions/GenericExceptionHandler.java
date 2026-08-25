package com.resume.backend.globalexceptions;

import com.resume.backend.helperclass.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GenericExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Exception occurred: {}", ex.getMessage(), ex);
        log.info("Exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(ApiResponse.builder().status(500).message(ex.getMessage()).build());
    }
}
