package com.resume.backend.controller;

import com.resume.backend.dtos.EditResumeDeatilsDto;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.services.EditResumeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/editResumeDetails")
//@PreAuthorize("hasRole('ADMIN')")
public class EditResumeDetailsController {
    @Autowired
    EditResumeDetailsService editResumeDetailsService;
    @PostMapping("/edit")
    public ResponseEntity<String> editResumeDetails(@RequestBody EditResumeDeatilsDto resumeTempDto){


        return ResponseEntity.ok(editResumeDetailsService.editResumeDetails(resumeTempDto));
    }
}
