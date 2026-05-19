package com.resume.backend.controller;

import com.resume.backend.helperclass.ApiResponse;
import com.resume.backend.helperclass.ProblemFactory;
import com.resume.backend.services.ResumeDeletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resume")
public class ResumeDeleteController {

    private final ResumeDeletionService resumeDeletionService;
    private ProblemFactory problemFactory;

    ResumeDeleteController(ResumeDeletionService resumeDeletionService, ProblemFactory problemFactory){
        this.resumeDeletionService=resumeDeletionService;
        this.problemFactory=problemFactory;
    }
    @DeleteMapping("/delete/{resumeId}")
    public ResponseEntity<ApiResponse> deleteResume(@PathVariable Long resumeId){
        String response = resumeDeletionService.deleteResume(resumeId);
        ApiResponse apiResponse = ApiResponse.builder().message(response).build();

        return new ResponseEntity<ApiResponse>(problemFactory.customResponse(true, "Resume Deleted successfully."), HttpStatus.OK);
    }
}
