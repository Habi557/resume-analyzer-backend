package com.resume.backend.globalexceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.resume.backend.exceptions.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.thymeleaf.exceptions.TemplateInputException;

import java.io.FileNotFoundException;
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
    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ProblemDetail> handleInvalidEmailException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid Email");
        problemDetail.setDetail(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }
    @ExceptionHandler(TemplateInputException.class)
    public ResponseEntity<ProblemDetail> handleTemplateInputException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Template Input Exception");
        problemDetail.setDetail(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }
    @ExceptionHandler(InvaidFileFormatException.class)
    public ResponseEntity<ProblemDetail> handleInvaidFileFormatException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Unsupported File Format");
        problemDetail.setDetail(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemDetail);
    }
    @ExceptionHandler(AiNotRespondingException.class)
    public ResponseEntity<String> handleAiNotResponding(AiNotRespondingException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.getMessage());
    }
    @ExceptionHandler(FileNotFoundEx.class)
    public ResponseEntity<String> handleFileNotFoundEx(FileNotFoundEx ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage()+"Message from Habi");
    }


}
