package com.resume.backend.serviceImplementation;

import com.resume.backend.services.EmailSender;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@Profile({"dev","docker"})
public class LocalEmailSender implements EmailSender {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

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
    // EmailSender.java
    public void sendHtmlEmailWithAttachment(String to, String subject, String htmlContent,
                                            String attachmentName, InputStream fileStream) throws MessagingException {
        byte[] fileBytes;
        try {
            fileBytes = fileStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read attachment stream", e);
        }

        MimeMessagePreparator message = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(to);
            helper.setFrom(fromEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.addAttachment(attachmentName, new ByteArrayResource(fileBytes)); // ✅ fixed: attachmentName not fileName
        };
        mailSender.send(message);
    }
}

