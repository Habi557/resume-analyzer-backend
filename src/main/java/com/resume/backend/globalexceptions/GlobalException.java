package com.resume.backend.globalexceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.resume.backend.exceptions.JsonProcessingRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler({JsonProcessingRuntimeException.class})
    public ProblemDetail handleJsonFormatException(String message, JsonProcessingException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatusCode.valueOf(400));
        problemDetail.setTitle("Json Exception");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("errorCode","Invalid json");
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;

    }
    @ExceptionHandler(JsonProcessingException.class)
    public ProblemDetail handleJsonProcessingException(JsonProcessingException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid JSON Format");
        problemDetail.setDetail(ex.getOriginalMessage());
        return problemDetail;
    }
}
