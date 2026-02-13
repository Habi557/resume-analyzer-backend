package com.resume.backend.serviceImplementation;

import com.resume.backend.services.EmailSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@Profile("prod")
public class SesEmailSender implements EmailSender {

    private final SesClient sesClient;

    public SesEmailSender(SesClient sesClient) {
        this.sesClient = sesClient;
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
}

