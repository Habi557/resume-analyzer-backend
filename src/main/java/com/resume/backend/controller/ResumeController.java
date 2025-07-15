package com.resume.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.exceptions.JsonProcessingRuntimeException;
import com.resume.backend.services.ResumeService;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/ai")
public class ResumeController {
    @Autowired
    ResumeService resumeService;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(@RequestParam(value = "userId",required = false) Long userId, @RequestParam("file")MultipartFile file){
        try {
            Resume resume = resumeService.uploadResume(userId, file);
           // return ResponseEntity.ok("Resume uploaded successfully. ID: " + resume.getId());
            return new ResponseEntity<String>("Resume uploaded successfully. ID: " + resume.getId(),HttpStatus.OK);
        }
        catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Resume is null");
        }
//        catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Upload failed: " + e.getMessage());
//        }

    }

    @PostMapping("/screen-resume")
    public ResponseEntity<List<ResumeAnalysisDTO>> resumeScreen(@RequestBody Map<String, String> requestBody, @RequestParam(value = "scanAllresumesIsChecked", defaultValue = "false") boolean scanAllresumesIsChecked)  {
        List<ResumeAnalysisDTO> screenedResult = null;
        screenedResult = resumeService.resumeScreen(requestBody.get("jobDescription"),scanAllresumesIsChecked);
            return  new ResponseEntity<List<ResumeAnalysisDTO>>(screenedResult,HttpStatus.OK);

       // return ResponseEntity.ok(screenedResult);
    }
    @GetMapping("/getAllAnalysiedResumes")
    public  ResponseEntity<List<ResumeAnalysisDTO>> getAllAnalysiedResumes(@RequestParam(name="pageNo" ,required = false,defaultValue = "0") int pageNo, @RequestParam(name="pageSize" ,required = false,defaultValue = "5") int pageSize){
        List<ResumeAnalysisDTO> allAnalysiedResumes = resumeService.getAllAnalysiedResumes(pageNo,pageSize);
        return ResponseEntity.ok(allAnalysiedResumes);
    }
    @GetMapping("/gellAllDashboardDetails")
    public ResponseEntity<DashboardDto> getAllDashboardDetails(){
       // return  ResponseEntity.ok(resumeService.getAllDashboardDetails());
        return  new ResponseEntity<DashboardDto>(resumeService.getAllDashboardDetails(),HttpStatus.OK);
    }
    @GetMapping("/downloadResume/{resumeId}")
    public  ResponseEntity<Resource> dowloadResume(@PathVariable long resumeId){
       Resource resource= resumeService.dowloadResume(resumeId);
       return new ResponseEntity<Resource>(resource,HttpStatus.OK);
    }
    @GetMapping("/allResumes")
    public ResponseEntity<List<Resume>> getAllResumes(){
        List<Resume> listofResume =resumeService.getAllResumes();
        return new ResponseEntity<List<Resume>>(listofResume,HttpStatus.OK);
    }


}
