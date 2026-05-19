package com.resume.backend.services;

import java.io.IOException;
import java.io.InputStream;

public interface ResumeParser {
    public String extractText(InputStream inputStream);
}
