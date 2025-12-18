package com.resume.backend.controller;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/getUserAnalyisedDetails")
    public ResponseEntity<List<ResumeAnalysisDTO>> getAllUsers(@RequestParam String username){
        return ResponseEntity.ok(userService.getAllUsersAnalysizedResumes(username));
    }
}
