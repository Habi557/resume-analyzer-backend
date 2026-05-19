package com.resume.backend.serviceImplementation;

import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.exceptions.FileNotFoundEx;
import com.resume.backend.exceptions.InvalidEmailException;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.EmailSender;
import com.resume.backend.services.EmailService;
import com.resume.backend.services.JitsiMeetingService;
import com.resume.backend.services.StorageService;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class EmailServiceImplementation implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private ResumeRepository resumeRepository;
    private ResumeAnalysis resumeAnalysisRepo;
    private ResumeHelper resumeHelper;
    private JitsiMeetingService jitsiMeetingService;
    private EmailSender emailSender;
    private StorageService storageService;


    @Autowired
     public  EmailServiceImplementation(JavaMailSender mailSender, TemplateEngine templateEngine, ResumeRepository resumeRepository,ResumeAnalysis resumeAnalysisRepo, ResumeHelper resumeHelper,JitsiMeetingService jitsiMeetingService,EmailSender emailSender,StorageService storageService) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.resumeRepository= resumeRepository;
        this.resumeAnalysisRepo=resumeAnalysisRepo;
        this.resumeHelper=resumeHelper;
        this.jitsiMeetingService=jitsiMeetingService;
        this.emailSender=emailSender;
        this.storageService=storageService;
    }

@EventListener
public void sendRegistrationEmail(UserEntity userEntity){
        HashMap<String,Object> map = new HashMap();
        map.put("name",userEntity.getUsername());
        String subject = "Registration Successful";
        Context context = new Context();
        context.setVariables(map);
        String htmlContent = templateEngine.process("registration-success", context);
        String emailTo = userEntity.getEmail();
        emailSender.sendHtmlEmail(emailTo, subject, htmlContent);
        log.info("Email sent to {}", emailTo);



}
public boolean sendInterviewStatusEmail(Long id, String templateName, String interviewDate, String interviewTime, String interviewMode) {

    ResumeAnalysisEntity entity = resumeAnalysisRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Resume not found with id: " + id));

    Resume resume = entity.getResume();
    String emailTo = resume.getEmail();
    InputStream resumeStream;
    if (!resumeHelper.isValidGmail(emailTo)) {
        throw new InvalidEmailException("Invalid Email");
    }
    try {
        resumeStream = storageService.loadFile(resume.getFilePath());
    } catch (IOException e) {
        throw new FileNotFoundEx("Failed to load resume file");
    }
    entity.setInterviewDate(interviewDate);
    entity.setInterviewTime(interviewTime);
    entity.setInterviewMode(interviewMode);
    entity.setSelectedStatus(templateName);

    Map<String, Object> model = new HashMap<>();
    model.put("name", resume.getName());
    model.put("interviewDate", interviewDate);
    model.put("interviewTime", interviewTime);
    model.put("interviewMode", interviewMode);
    String meetingLink = jitsiMeetingService.generateMeetingLink(resume.getUser().getUsername(), resume.getName());
    Context context = new Context();
    context.setVariables(model);
    context.setVariable("confirmationLink", meetingLink);
    String ModfiedtemplateName = resolveTemplatename(templateName);
    String htmlContent = templateEngine.process(ModfiedtemplateName, context);
    String subject = resolveSubject(templateName);
    String fileName = Paths.get(resume.getFilePath()).getFileName().toString();

    //emailSender.sendHtmlEmail(emailTo, subject, htmlContent);
    try {
        emailSender.sendHtmlEmailWithAttachment(emailTo, subject, htmlContent, fileName, resumeStream);
    } catch (MessagingException e) {
        throw new RuntimeException("Failed to send email with attachment", e);
    }

    resumeAnalysisRepo.save(entity);
    return true;
}

    private String resolveSubject(String templateName) {
        return switch (templateName) {
            case "Scheduled for Interview" -> "Interview Scheduled";
            case "Selected" -> "Congratulations! You’ve Been Selected";
            default -> "Update on Your Application";
        };
    }
    private String resolveTemplatename(String templateName){
       return switch (templateName){
           case "Scheduled for Interview" -> "interview_scheduled";
           case "Selected" -> "selected";
           case "Rejected" -> "rejected";
           default -> "update";
       };
    }
}
