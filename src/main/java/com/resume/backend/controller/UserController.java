package com.resume.backend.controller;

import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.UserDto;
import com.resume.backend.entity.UserEntity;
import com.resume.backend.helperclass.ApiResponse;
import com.resume.backend.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/getUserAnalyisedDetails")
    public ResponseEntity<List<ResumeAnalysisDTO>> getAllUsers(@RequestParam String username){
        return ResponseEntity.ok(userService.getAllUsersAnalysizedResumes(username));
    }
    @GetMapping("/getUser/{username}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable String username){

        UserDto user = userService.getUser(username);
        return ResponseEntity.ok(ApiResponse.builder().success(true).status(200).message("User found").data(user).build());
    }
}
