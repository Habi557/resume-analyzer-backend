package com.resume.backend.helperclass;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ProblemFactory {

    public ProblemDetail badRequest(String code, String title, String detail){
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle(title);
        pd.setDetail(detail);
        pd.setProperty("code", code);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    public ProblemDetail internal(String code, String title){
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle(title);
        pd.setDetail("Unexpected error");
        pd.setProperty("code", code);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
    public  ApiResponse customResponse(boolean fail, String message ){
        return ApiResponse.builder().success(fail).message(message).build();
    }
}

