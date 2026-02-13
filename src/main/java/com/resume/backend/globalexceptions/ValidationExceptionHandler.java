package com.resume.backend.globalexceptions;

import com.resume.backend.exceptions.NoRoleFoundExcepiton;
import com.resume.backend.exceptions.UserAlreadyExistsException;
import com.resume.backend.helperclass.ProblemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidationExceptionHandler {
    @Autowired
    private ProblemFactory problemFactory;
   @ExceptionHandler(UserAlreadyExistsException.class)
   public ResponseEntity<ProblemDetail> handleUserAlreadyExistsException(UserAlreadyExistsException ex){
     return ResponseEntity.badRequest()
             .contentType(MediaType.APPLICATION_PROBLEM_JSON)
             .body(problemFactory.badRequest("400","User already exists",ex.getMessage()));
   }
   @ExceptionHandler(NoRoleFoundExcepiton.class)
    public ResponseEntity<ProblemDetail> handleNoRoleFoundExcepiton(NoRoleFoundExcepiton ex){
        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problemFactory.badRequest("400","No role found",ex.getMessage()));
    }
}
