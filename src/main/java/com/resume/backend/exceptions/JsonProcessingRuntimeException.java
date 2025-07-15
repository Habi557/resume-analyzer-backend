package com.resume.backend.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonProcessingRuntimeException extends RuntimeException{

    private JsonProcessingException cause;
    public  JsonProcessingRuntimeException(String message){
        super(message);
    }
}
