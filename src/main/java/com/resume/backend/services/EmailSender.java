package com.resume.backend.services;

import jakarta.mail.MessagingException;

import java.io.InputStream;

public interface EmailSender {
    void sendHtmlEmail(String to, String subject, String htmlContent);
    public void sendHtmlEmailWithAttachment(String to, String subject, String htmlContent, String attachmentName, InputStream attachmentStream) throws MessagingException;
}

