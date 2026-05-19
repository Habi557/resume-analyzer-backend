package com.resume.backend.services;

import java.util.List;

public interface Suggestions {
    List<String> getSuggestions(String query);

}
