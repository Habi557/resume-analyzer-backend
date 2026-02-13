package com.resume.backend.serviceImplementation;

import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.exceptions.InvalidEmailException;
import com.resume.backend.helperclass.ResumeHelper;
import com.resume.backend.repository.ResumeAnalysis;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.EmailSender;
import com.resume.backend.services.EmailService;
import com.resume.backend.services.JitsiMeetingService;
import jakarta.mail.Message;
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


    @Autowired
     public  EmailServiceImplementation(JavaMailSender mailSender, TemplateEngine templateEngine, ResumeRepository resumeRepository,ResumeAnalysis resumeAnalysisRepo, ResumeHelper resumeHelper,JitsiMeetingService jitsiMeetingService,EmailSender emailSender) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.resumeRepository= resumeRepository;
        this.resumeAnalysisRepo=resumeAnalysisRepo;
        this.resumeHelper=resumeHelper;
        this.jitsiMeetingService=jitsiMeetingService;
        this.emailSender=emailSender;
    }

//    public boolean sendEmail(Long id, String templateName, String interviewDate, String interviewTime, String interviewMode ) {
//        // String to, String subject, String templateName, Map<String, Object> model
//        ResumeAnalysisEntity resumeAnlysisEntity = this.resumeAnalysisRepo.findById(id).get();
//        System.out.println("Template name: " + templateName);
//
//        Resume resume = resumeAnlysisEntity.getResume();
//        String emailTo = resume.getEmail();
//        MimeMessagePreparator message = null;
//        Map<String, Object> model = new HashMap<>();
//        model.put("name", resume.getName());
//        model.put("interviewDate",interviewDate);
//        model.put("interviewTime",interviewTime);
//        model.put("interviewMode",interviewMode);
//        resumeAnlysisEntity.setInterviewDate(interviewDate);
//        resumeAnlysisEntity.setInterviewTime(interviewTime);
//        resumeAnlysisEntity.setInterviewMode(interviewMode);
//        resumeAnlysisEntity.setSelectedStatus(templateName);
//        String subject = switch (templateName) {
//            case "interview_scheduled" -> "Interview Scheduled";
//            case "selected" -> "Congratulations! You’ve Been Selected";
//            default -> "Update on Your Application";
//        };
//        if(emailTo != null && !emailTo.isEmpty() && resumeHelper.isValidGmail(emailTo)){
//            Context context = new Context();
//            context.setVariables(model);
//            templateName = templateName.trim();
//            try {
//                String meetingLink = jitsiMeetingService.generateMeetingLink("venkat", resume.getName());
//                context.setVariable("confirmationLink", meetingLink);
//                if(templateName.equals("Scheduled for Interview")){
//                    templateName = "interview_scheduled";
//                }else if(templateName.equals("Rejected")){
//                    templateName="rejected";
//                }else if(templateName.equals("Selected")){
//                    templateName = "selected";
//                }
//                String htmlContent = templateEngine.process(templateName, context);
//                message = mimeMessage -> {
//                    mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
//                    mimeMessage.setFrom(new InternetAddress("habibullashaik9944@gmail.com"));
//                    mimeMessage.setSubject(subject);
//                    mimeMessage.setContent(htmlContent, "text/html");
//                };
//                mailSender.send(message);
//            this.resumeAnalysisRepo.save(resumeAnlysisEntity);
//            return true;
//            }catch (TemplateInputException e){
//                throw  new TemplateInputException(" Email Template not found");
//            }
//            catch (MailException e){
//                throw new RuntimeException("Ioexception occured");
//            }
////            catch (GeneralSecurityException e){
////                throw new RuntimeException("GeneralSecurityException occured");
////            }
//
//        }else{
//            throw  new InvalidEmailException("Invalid Email");
//        }
//
//
//    }
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
public boolean sendEmail(Long id, String templateName, String interviewDate, String interviewTime, String interviewMode) {

    ResumeAnalysisEntity entity = resumeAnalysisRepo.findById(id)
            .orElseThrow();

    Resume resume = entity.getResume();
    String emailTo = resume.getEmail();

    if (!resumeHelper.isValidGmail(emailTo)) {
        throw new InvalidEmailException("Invalid Email");
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


    Context context = new Context();
    context.setVariables(model);
    String meetingLink = jitsiMeetingService.generateMeetingLink(resume.getUser().getUsername(), resume.getName());
    context.setVariable("confirmationLink", meetingLink);
    String ModfiedtemplateName = resolveTemplatename(templateName);
    String htmlContent = templateEngine.process(ModfiedtemplateName, context);
    String subject = resolveSubject(templateName);

    emailSender.sendHtmlEmail(emailTo, subject, htmlContent);

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
