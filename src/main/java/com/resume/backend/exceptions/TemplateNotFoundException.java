package com.resume.backend.exceptions;

import java.io.IOException;

public class TemplateNotFoundException extends IOException {
    private final String templatePath;

    // Constructor with template path
    public TemplateNotFoundException(String templatePath) {
        super("Template not found at path: " + templatePath);
        this.templatePath = templatePath;
    }

    // Constructor with template path and cause
    public TemplateNotFoundException(String templatePath, Throwable cause) {
        super("Template not found at path: " + templatePath, cause);
        this.templatePath = templatePath;
    }
}
