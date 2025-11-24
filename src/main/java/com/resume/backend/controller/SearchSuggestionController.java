package com.resume.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.Skill;
import com.resume.backend.repository.SkillRepository;
import com.resume.backend.services.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/search")
public class SearchSuggestionController {
    private ResumeService resumeService;
    private SkillRepository skillRepository;

    SearchSuggestionController(ResumeService resumeService, SkillRepository skillRepository) {
        this.resumeService = resumeService;
        this.skillRepository = skillRepository;
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam("query") String query) {
        {
            System.out.println("query "+query);
            List<String> suggestions = resumeService.getSuggestions(query);
            List<String> skillSuggestions = this.skillRepository.findSkillSuggestions(query);
            List<String> combined = Stream.concat(suggestions.stream(), skillSuggestions.stream())
//                    .distinct()
//                    .limit(10)
                    .toList();
            return ResponseEntity.ok(combined);

        }
    }

    @GetMapping("/analysedresumes")
    public ResponseEntity<List<ResumeAnalysisDTO>> findResumesBySkillName(@RequestParam("") String skillName,@RequestParam int currentPage, @RequestParam int pageSize ) {
        List<ResumeAnalysisDTO> collect = resumeService.findResumesBySkillName(skillName, currentPage, pageSize)
                .stream()
                .map(Resume::getResumeAnalysisList)
                .flatMap(List::stream)
                .map(ra -> {
                   // ResumeAnalysisDTO resumeAnalysisDTO = new ObjectMapper().convertValue(ra, ResumeAnalysisDTO.class);
                    ResumeAnalysisDTO analysisDTO = new ResumeAnalysisDTO(ra.getId(), ra.getMatchPercentage(), ra.getSuggestions(), ra.getConclusion(), ra.getAnalysizedTime(), ra.getTopMatchingSkills(), ra.getResume(), ra.getInterviewDate(), ra.getInterviewTime(), ra.getSelectedStatus());
                    return analysisDTO;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<List<ResumeAnalysisDTO>>(collect,HttpStatus.OK);
       // return null;

    }
    @GetMapping("/test")
    public  String test(){
        return "My test";
    }
}
