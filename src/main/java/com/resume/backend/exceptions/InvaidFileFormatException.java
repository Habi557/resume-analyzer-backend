package com.resume.backend.exceptions;

public class InvaidFileFormatException extends RuntimeException {
    private String message;;
    public InvaidFileFormatException(String message) {
        this.message=message;
    }
}
