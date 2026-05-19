package com.resume.backend.serviceImplementation;

import com.resume.backend.services.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.io.IOException;
import java.io.InputStream;

@Service
@Profile("prod")
public class SesEmailSender implements EmailSender {

    private final SesClient sesClient;
    private EmailSender emailSender;

    public SesEmailSender(SesClient sesClient,EmailSender emailSender) {
        this.sesClient = sesClient;
        this.emailSender=emailSender;
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {

        SendEmailRequest request = SendEmailRequest.builder()
                .source("habibullashaik9944@gmail.com") // verified in SES
                .destination(Destination.builder()
                        .toAddresses(to)
                        .build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder()
                                .html(Content.builder().data(htmlContent).build())
                                .build())
                        .build())
                .build();

        sesClient.sendEmail(request);
    }
    // EmailSender.java
    public void sendHtmlEmailWithAttachment(
            String to, String subject, String htmlContent,
            String attachmentName, InputStream attachmentStream
    ) throws MessagingException {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//        helper.setTo(to);
//        helper.setSubject(subject);
//        helper.setText(htmlContent, true); // true = isHtml
//
//        // Add attachment
//        helper.addAttachment(attachmentName, new InputStreamSource() {
//            @Override
//            public InputStream getInputStream() throws IOException {
//                return attachmentStream;
//            }
//        });
//
//        mailSender.send(message);
    }
}

