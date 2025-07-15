package com.resume.backend.helperclass;

import com.resume.backend.dtos.ResumeAnalysisDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResumeAnalysisParser {

 //   public static ResumeAnalysisDTO parse(String aiResponse) {
//        ResumeAnalysisDTO dto = new ResumeAnalysisDTO();
//
//        String[] lines = aiResponse.split("\\n");
//
//        for (String line : lines) {
//            if (line.startsWith("1. Match Percentage:")) {
//                dto.setMatchPercentage(
//                        Integer.parseInt(line.replaceAll("[^0-9]", ""))
//                );
//            } else if (line.startsWith("2. Extracted Skills:")) {
//                String skillsStr = line.replace("2. Extracted Skills:", "").trim();
//                dto.setSkills(
//                        Arrays.stream(skillsStr.split(","))
//                                .map(String::trim)
//                                .toList()
//                );
//            } else if (line.startsWith("3. Years of Experience:")) {
//                dto.setYearsOfExperience(
//                        Double.parseDouble(line.replaceAll("[^0-9.]", ""))
//                );
//            } else if (line.startsWith("4. Suggestions:")) {
//                List<String> suggestions = new ArrayList<>();
//            } else if (line.startsWith("- ")) {
//                dto.getSuggestions().add(line.replace("- ", "").trim());
//            } else if (line.startsWith("5. Final Summary:")) {
//                dto.setConclusion(line.replace("5. Final Summary:", "").trim());
//            }
//        }
//
//        return dto;
//    }
}

