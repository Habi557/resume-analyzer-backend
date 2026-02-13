package com.resume.backend.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String error) {
        super(error);
    }
}
