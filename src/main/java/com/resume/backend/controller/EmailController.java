package com.resume.backend.controller;

import com.resume.backend.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {
    @Autowired
    EmailService emailService;
    @GetMapping("/sendEmail/{id}")
    public ResponseEntity<String> sendEmail(@PathVariable("id") Long id, @RequestParam String templateName){
        boolean emailStatus = emailService.sendEmail(id, templateName);
        if (emailStatus){
            return new ResponseEntity<String>("Email sent Successfully", HttpStatus.OK);

        }else {
            return new ResponseEntity<String>("Email Not sent", HttpStatus.OK);

        }
    }
}
