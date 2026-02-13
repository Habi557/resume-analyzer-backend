package com.resume.backend.services;

import com.resume.backend.entity.UserEntity;

public interface EmailService {
    public boolean sendEmail(Long id,String templateName, String interviewDate, String interviewTime, String interviewMode);
    public void sendRegistrationEmail(UserEntity userEntity);

    }
