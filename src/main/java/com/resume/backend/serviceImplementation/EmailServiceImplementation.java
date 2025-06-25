package com.resume.backend.serviceImplementation;

import com.resume.backend.entity.Resume;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.EmailService;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class EmailServiceImplementation implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private ResumeRepository resumeRepository;

    @Autowired
     public  EmailServiceImplementation(JavaMailSender mailSender, TemplateEngine templateEngine, ResumeRepository resumeRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.resumeRepository= resumeRepository;
    }

    public boolean sendEmail(Long id, String templateName ) {
        // String to, String subject, String templateName, Map<String, Object> model
        Optional<Resume> resumeById = this.resumeRepository.findById(id);
        System.out.println("Template name: " + templateName);

        Resume resume = resumeById.get();
        String emailTo = resume.getEmail();
        MimeMessagePreparator message = null;
        Map<String, Object> model = new HashMap<>();
        model.put("name", resume.getName());
        String subject="Just for testing the email";
        if(emailTo != null && !emailTo.isEmpty()){
            Context context = new Context();
            context.setVariables(model);
            templateName = templateName.trim();
           // try {
                String htmlContent = templateEngine.process(templateName, context);
                message = mimeMessage -> {
                    mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
                    mimeMessage.setFrom(new InternetAddress("habibullashaik9944@gmail.com"));
                    mimeMessage.setSubject(subject);
                    mimeMessage.setContent(htmlContent, "text/html");
                };
                mailSender.send(message);
                return true;
//            }catch (TemplateInputException e){
//                throw  new RuntimeException(" Email Template not found");
//            }

        }else{
            throw new RuntimeException("No email found");
        }


    }
}
