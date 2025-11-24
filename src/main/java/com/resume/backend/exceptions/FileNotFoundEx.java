package com.resume.backend.exceptions;

public class FileNotFoundEx extends RuntimeException {
    public FileNotFoundEx(String message) {
        super(message);
    }
}
