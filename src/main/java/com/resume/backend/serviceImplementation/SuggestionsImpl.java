package com.resume.backend.serviceImplementation;

import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.Suggestions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SuggestionsImpl implements Suggestions {
    private final ResumeRepository resumeRepository;
    SuggestionsImpl(ResumeRepository resumeRepository){
        this.resumeRepository=resumeRepository;
    }
    // @Cacheable(value ="suggestionSkills", key = "#query")
    @Override
    public List<String> getSuggestions(String query) {
        List<String> suggestions = resumeRepository.getSuggestions(query);
        return suggestions;
    }

}
