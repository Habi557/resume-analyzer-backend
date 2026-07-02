package com.resume.backend.globalexceptions;

import com.resume.backend.exceptions.NoRoleFoundExcepiton;
import com.resume.backend.exceptions.UserAlreadyExistsException;
import com.resume.backend.helperclass.ApiResponse;
import com.resume.backend.helperclass.ProblemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.UnknownHostException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ValidationExceptionHandler {
    @Autowired
    private ProblemFactory problemFactory;
   @ExceptionHandler(UserAlreadyExistsException.class)
   public ResponseEntity<ApiResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex){
     return ResponseEntity.badRequest()
             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
             .body(ApiResponse.builder().success(false).status(500).message(ex.getMessage()).build());
   }
   @ExceptionHandler(NoRoleFoundExcepiton.class)
    public ResponseEntity<ProblemDetail> handleNoRoleFoundExcepiton(NoRoleFoundExcepiton ex){
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemFactory.badRequest("400","No role found",ex.getMessage()));
    }
    @ExceptionHandler(MailException.class)
    public ResponseEntity<ApiResponse> handleInvalidEmailException(MailException ex){
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(ApiResponse.builder().success(false).status(500).message("Email is not a valid please check again").build());
    }

}
