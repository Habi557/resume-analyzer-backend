package com.resume.backend.exceptions;

public class StoredProcedureNotFound extends RuntimeException {
    public StoredProcedureNotFound(String message) {
        super(message);
    }
}
