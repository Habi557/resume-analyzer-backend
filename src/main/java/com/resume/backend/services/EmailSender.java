package com.resume.backend.services;

public interface EmailSender {
    void sendHtmlEmail(String to, String subject, String htmlContent);
}

