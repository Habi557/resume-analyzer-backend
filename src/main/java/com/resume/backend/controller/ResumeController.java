package com.resume.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.dtos.FileDownloadDataDto;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.ResumeProjectionDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.exceptions.JsonProcessingRuntimeException;
import com.resume.backend.helperclass.ApiResponse;
import com.resume.backend.helperclass.ProblemFactory;
import com.resume.backend.projection.ResumeProjection;
import com.resume.backend.services.DashboardDeatils;
import com.resume.backend.services.ResumeAnalysisService;
import com.resume.backend.services.ResumeSearchService;
import com.resume.backend.services.ResumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Slf4j
public class ResumeController {
    @Autowired
    ResumeService resumeService;
    @Autowired
    ProblemFactory problemFactory;
    @Autowired
    ResumeAnalysisService resumeAnalysisService;
    @Autowired
    ResumeSearchService resumeSearchService;
    @Autowired
    DashboardDeatils dashboardDeatils;
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadResume(@RequestParam(value = "username",required = true) String username, @RequestParam("file")MultipartFile file) {
        try {
            Resume resume = resumeService.uploadResume(username, file);
            return new ResponseEntity<ApiResponse>(problemFactory.customResponse(true, "Resume uploaded successfully. ID: " + resume.getId()), HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<ApiResponse>(problemFactory.customResponse(false, "Resume uploaded failed"), HttpStatus.BAD_REQUEST);


        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/screen-resume")
    public ResponseEntity<Map<String, String>> resumeScreen(@RequestBody Map<String, String> requestBody, @RequestParam(value = "scanAllresumesIsChecked", defaultValue = "false") boolean scanAllresumesIsChecked)  {
        List<ResumeAnalysisDTO> screenedResult = null;
        String jobId = resumeAnalysisService.analysisResumeWithJd(requestBody.get("jobDescription"), scanAllresumesIsChecked);
        return ResponseEntity.accepted().body(Map.of(
                "jobId", jobId,
                "message", "Analysis started. Poll /api/resume-analysis/status/" + jobId
        ));
       // return ResponseEntity.ok(screenedResult);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllAnalysiedResumes")
    public  ResponseEntity<List<ResumeAnalysisDTO>> getAllAnalysiedResumes(@RequestParam(required = true,defaultValue = "0") int pageNo, @RequestParam(name="pageSize" ,required = false,defaultValue = "5") int pageSize,@RequestParam(name="sortBy",required = false,defaultValue = "bestMatch") String sort,@RequestParam(name = "sortingDirection",required = false, defaultValue= "ASC") String dir){
        List<ResumeAnalysisDTO> allAnalysiedResumes = resumeSearchService.getAllAnalysiedResumes(pageNo,pageSize,sort,dir);
        return ResponseEntity.ok(allAnalysiedResumes);
    }
    @GetMapping("/gellAllDashboardDetails")
    public ResponseEntity<DashboardDto> getAllDashboardDetails(){
       // return  ResponseEntity.ok(resumeService.getAllDashboardDetails());
        return  new ResponseEntity<DashboardDto>(dashboardDeatils.getAllDashboardDetails(),HttpStatus.OK);
    }
    @GetMapping("/downloadResume/{resumeId}")
     public ResponseEntity<Resource> downloadResume(@PathVariable long resumeId) {

    FileDownloadDataDto data = resumeService.dowloadResume(resumeId);

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(data.getContentType()))
            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + data.getFileName() + "\""
            )
            .contentLength(data.getFileSize())
            .body(data.getResource());
}
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/allResumes")
    public ResponseEntity<List<ResumeProjectionDto>> getAllResumes(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "10") int pageSize){
        List<ResumeProjectionDto> listofResume =resumeSearchService.getAllResumes(pageNo,pageSize);
        this.log.debug("Method getAllResumes executed");

        return new ResponseEntity<List<ResumeProjectionDto>>(listofResume,HttpStatus.OK);
    }
    @GetMapping("/test")
    public  String test(){
        this.log.info("test log");
        return "test";
    }


}
