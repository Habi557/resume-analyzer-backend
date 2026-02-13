package com.resume.backend.helperclass;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class ApiResponse<T> {

    private boolean success;
    private  int status;
    private String message;
    private T data;
    private OffsetDateTime timestamp;
}
