package com.resume.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.dtos.FileDownloadDataDto;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.exceptions.JsonProcessingRuntimeException;
import com.resume.backend.projection.ResumeProjection;
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
    @PostMapping("/upload")
    public ResponseEntity<String> uploadResume(@RequestParam(value = "username",required = true) String username, @RequestParam("file")MultipartFile file){
        try {
            Resume resume = resumeService.uploadResume(username, file);
           // return ResponseEntity.ok("Resume uploaded successfully. ID: " + resume.getId());
            return new ResponseEntity<String>("Resume uploaded successfully. ID: " + resume.getId(),HttpStatus.OK);
        }
        catch (NullPointerException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Something went worng try agian");
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
    public  ResponseEntity<List<ResumeAnalysisDTO>> getAllAnalysiedResumes(@RequestParam(required = true,defaultValue = "0") int pageNo, @RequestParam(name="pageSize" ,required = false,defaultValue = "5") int pageSize){
        List<ResumeAnalysisDTO> allAnalysiedResumes = resumeService.getAllAnalysiedResumes(pageNo,pageSize);
        return ResponseEntity.ok(allAnalysiedResumes);
    }
    @GetMapping("/gellAllDashboardDetails")
    public ResponseEntity<DashboardDto> getAllDashboardDetails(){
       // return  ResponseEntity.ok(resumeService.getAllDashboardDetails());
        return  new ResponseEntity<DashboardDto>(resumeService.getAllDashboardDetails(),HttpStatus.OK);
    }
//    @GetMapping("/downloadResume/{resumeId}")
//    public  ResponseEntity<Resource> dowloadResume(@PathVariable long resumeId){
//       Resource resource= resumeService.dowloadResume(resumeId);
//       return new ResponseEntity<Resource>(resource,HttpStatus.OK);
//    }
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
    public ResponseEntity<List<ResumeProjection>> getAllResumes(){
        List<ResumeProjection> listofResume =resumeService.getAllResumes();
        this.log.debug("Method getAllResumes executed");

        return new ResponseEntity<List<ResumeProjection>>(listofResume,HttpStatus.OK);
    }
    @GetMapping("/test")
    public  String test(){
        this.log.info("test log");
        return "test";
    }


}
