package com.resume.backend.serviceImplementation;

import com.resume.backend.repository.ResumeRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SuggestionsImplTest {

    @Test
    void getSuggestions_delegatesToRepository() {
        AtomicReference<String> receivedQuery = new AtomicReference<>();
        List<String> suggestions = List.of("java", "javascript");
        ResumeRepository resumeRepository = repositoryReturningSuggestions(receivedQuery, suggestions);
        SuggestionsImpl service = new SuggestionsImpl(resumeRepository);

        List<String> result = service.getSuggestions("jav");

        assertEquals("jav", receivedQuery.get());
        assertEquals(suggestions, result);
    }

    private ResumeRepository repositoryReturningSuggestions(AtomicReference<String> receivedQuery, List<String> suggestions) {
        return (ResumeRepository) Proxy.newProxyInstance(
                ResumeRepository.class.getClassLoader(),
                new Class<?>[]{ResumeRepository.class},
                (proxy, method, args) -> {
                    if ("getSuggestions".equals(method.getName())) {
                        receivedQuery.set((String) args[0]);
                        return suggestions;
                    }
                    if ("toString".equals(method.getName())) {
                        return "ResumeRepository test proxy";
                    }
                    throw new UnsupportedOperationException("Unexpected method call: " + method.getName());
                }
        );
    }
}
