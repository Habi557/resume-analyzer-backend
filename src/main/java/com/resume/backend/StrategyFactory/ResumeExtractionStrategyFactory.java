package com.resume.backend.StrategyFactory;

import com.resume.backend.serviceImplementation.DocxResumeParser;
import com.resume.backend.serviceImplementation.PdfResumeParser;
import com.resume.backend.services.ResumeParser;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ResumeExtractionStrategyFactory {
    public final Map<String, ResumeParser> strategyMap;
    public ResumeExtractionStrategyFactory(Map<String,ResumeParser> strategyMap) {
        this.strategyMap = strategyMap;
    }
    public ResumeParser getStrategy(String fileName) {
        String extension = extractExtension(fileName);
        ResumeParser resumeParser = strategyMap.get(extension);
        if (resumeParser == null) {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
        return resumeParser;


    }
    private String extractExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
}
