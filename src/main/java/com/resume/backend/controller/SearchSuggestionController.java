package com.resume.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.dtos.ResumeAnalysisDTO;
import com.resume.backend.dtos.ResumeTempDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.entity.ResumeAnalysisEntity;
import com.resume.backend.entity.Skill;
import com.resume.backend.helperclass.ConvertingEntityToDtos;
import com.resume.backend.repository.SkillRepository;
import com.resume.backend.services.ResumeSearchService;
import com.resume.backend.services.ResumeService;
import com.resume.backend.services.Suggestions;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private  ConvertingEntityToDtos convertingEntityToDtos;
    private ResumeSearchService resumeSearchService;
    private SkillRepository skillRepository;
    private Suggestions suggestionsimpl;

    SearchSuggestionController(ResumeSearchService resumeSearchService, SkillRepository skillRepository,ConvertingEntityToDtos convertingEntityToDtos,Suggestions suggestions) {
        this.resumeSearchService = resumeSearchService;
        this.skillRepository = skillRepository;
        this.convertingEntityToDtos=convertingEntityToDtos;
        this.suggestionsimpl=suggestions;
    }

    @GetMapping(value = "/suggestions" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> getSuggestions(@RequestParam("query") String query) {
        {
            System.out.println("query "+query);
            List<String> suggestions = suggestionsimpl.getSuggestions(query);
            List<String> skillSuggestions = this.skillRepository.findSkillSuggestions(query);
            List<String> combined = Stream.concat(suggestions.stream(), skillSuggestions.stream())
//                    .distinct()
//                    .limit(10)
                    .toList();
            return ResponseEntity.ok(combined);

        }
    }

    @GetMapping("/analysedresumes")
    public ResponseEntity<List<ResumeAnalysisDTO>> findResumesBySkillName(@RequestParam("") String skillName,@RequestParam(defaultValue = "0") int currentPage, @RequestParam(defaultValue = "2") int pageSize ) {
        List<ResumeAnalysisDTO> collect = resumeSearchService.findResumesBySkillName(skillName, currentPage, pageSize);

        return new ResponseEntity<List<ResumeAnalysisDTO>>(collect,HttpStatus.OK);
       // return null;

    }
    @GetMapping("/test")
    public  String test(){
        return "My test";
    }
}
