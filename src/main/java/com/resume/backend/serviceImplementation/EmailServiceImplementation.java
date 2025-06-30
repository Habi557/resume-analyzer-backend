package com.resume.backend.serviceImplementation;

import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.exceptions.InvalidEmailException;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.repository.ResumeAnalysis;
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
    private ResumeAnalysis resumeAnalysisRepo;
    private ResumeHelper resumeHelper;

    @Autowired
     public  EmailServiceImplementation(JavaMailSender mailSender, TemplateEngine templateEngine, ResumeRepository resumeRepository,ResumeAnalysis resumeAnalysisRepo, ResumeHelper resumeHelper) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.resumeRepository= resumeRepository;
        this.resumeAnalysisRepo=resumeAnalysisRepo;
        this.resumeHelper=resumeHelper;
    }

    public boolean sendEmail(Long id, String templateName, String interviewDate, String interviewTime, String interviewMode ) {
        // String to, String subject, String templateName, Map<String, Object> model
        ResumeAnalysisEntity resumeAnlysisEntity = this.resumeAnalysisRepo.findById(id).get();
        System.out.println("Template name: " + templateName);

        Resume resume = resumeAnlysisEntity.getResume();
        String emailTo = resume.getEmail();
        MimeMessagePreparator message = null;
        Map<String, Object> model = new HashMap<>();
        model.put("name", resume.getName());
        model.put("interviewDate",interviewDate);
        model.put("interviewTime",interviewTime);
        model.put("interviewMode",interviewMode);
        resumeAnlysisEntity.setInterviewDate(interviewDate);
        resumeAnlysisEntity.setInterviewTime(interviewTime);
        resumeAnlysisEntity.setInterviewMode(interviewMode);
        resumeAnlysisEntity.setSelectedStatus(templateName);
        String subject = switch (templateName) {
            case "interview_scheduled" -> "Interview Scheduled";
            case "selected" -> "Congratulations! You’ve Been Selected";
            default -> "Update on Your Application";
        };
        if(emailTo != null && !emailTo.isEmpty() && resumeHelper.isValidGmail(emailTo)){
            Context context = new Context();
            context.setVariables(model);
            templateName = templateName.trim();
            try {
                String htmlContent = templateEngine.process(templateName, context);
                message = mimeMessage -> {
                    mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
                    mimeMessage.setFrom(new InternetAddress("habibullashaik9944@gmail.com"));
                    mimeMessage.setSubject(subject);
                    mimeMessage.setContent(htmlContent, "text/html");
                };
                mailSender.send(message);
            this.resumeAnalysisRepo.save(resumeAnlysisEntity);
            return true;
            }catch (TemplateInputException e){
                throw  new TemplateInputException(" Email Template not found");
            }

        }else{
            throw  new InvalidEmailException("Invalid Email");
        }


    }
}
