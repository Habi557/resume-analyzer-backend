package com.resume.backend.services;

public interface EmailService {
    public boolean sendEmail(Long id,String templateName, String interviewDate, String interviewTime, String interviewMode);

    }
