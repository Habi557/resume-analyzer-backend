package com.resume.backend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonProcessingRuntimeException extends RuntimeException{
    private String message;
    private JsonProcessingException cause;
    public  JsonProcessingRuntimeException(String message, JsonProcessingException cause){
        super(message,cause);
        this.message=message;
        this.cause=cause;
    }
}
