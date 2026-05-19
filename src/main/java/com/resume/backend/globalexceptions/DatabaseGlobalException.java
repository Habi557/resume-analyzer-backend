package com.resume.backend.globalexceptions;

import com.resume.backend.exceptions.StoredProcedureNotFound;
import com.resume.backend.helperclass.ApiResponse;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLSyntaxErrorException;

@ControllerAdvice
public class DatabaseGlobalException {
    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<ApiResponse> handleStoredProcedureNotFound(InvalidDataAccessResourceUsageException ex) {
        String message = "Database Procedure/query error";
        if(ex.getCause()  instanceof SQLSyntaxErrorException sqlEx && sqlEx.getMessage().contains("does not exist")){
            message="Required stored procedure not found: "+ sqlEx.getMessage();
        }
        return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(ApiResponse.builder().status(503).message(message).build());

    }
}
