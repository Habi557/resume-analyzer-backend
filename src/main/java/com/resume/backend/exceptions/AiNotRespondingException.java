package com.resume.backend.exceptions;

public class AiNotRespondingException extends RuntimeException {
    public AiNotRespondingException(String message) {
        super(message);
    }
}
