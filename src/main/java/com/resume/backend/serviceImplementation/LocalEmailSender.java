package com.resume.backend.serviceImplementation;

import com.resume.backend.services.EmailSender;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev","docker"})
public class LocalEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    public LocalEmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {

        MimeMessagePreparator message = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setFrom(new InternetAddress("habibullashaik9944@gmail.com"));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(htmlContent, "text/html");
        };

        mailSender.send(message);
    }
}

