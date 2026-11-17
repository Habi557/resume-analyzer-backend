package com.resume.backend.exceptions;

public class JobLaunchException extends RuntimeException{
    public JobLaunchException(String message) {
        super(message);
    }
    public JobLaunchException(String message, Throwable cause) {
        super(message, cause);
    }
    public JobLaunchException(Throwable cause) {
        super(cause);
    }
}
