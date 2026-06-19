package com.resume.backend.helperclass;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeHelperTest {

    private final ResumeHelper resumeHelper = new ResumeHelper();

    @Test
    void extractJson_removesThinkBlockAndMarkdownFence() {
        String content = """
                <think>internal notes</think>
                ```json
                {"name":"Habi"}
                ```
                """;

        String result = resumeHelper.extractJson(content);

        assertEquals("{\"name\":\"Habi\"}", result);
    }

    @Test
    void putValuesToPrompt_replacesNamedPlaceholders() {
        String result = resumeHelper.putValuesToPrompt(
                "Resume: {resumeText}, Job: {jobRole}",
                Map.of("resumeText", "Java developer", "jobRole", "Backend")
        );

        assertEquals("Resume: Java developer, Job: Backend", result);
    }

    @Test
    void isValidGmail_acceptsOnlyGmailAddresses() {
        assertTrue(resumeHelper.isValidGmail("candidate@gmail.com"));
        assertFalse(resumeHelper.isValidGmail("candidate@yahoo.com"));
    }

    @Test
    void detectSections_extractsKnownResumeSectionsInOrder() {
        String text = """
                SUMMARY
                Backend developer
                SKILLS
                Java, Spring
                EXPERIENCE
                Built APIs
                """;

        Map<ResumeSection, String> result = resumeHelper.detectSections(text);

        assertEquals(3, result.size());
        assertTrue(result.get(ResumeSection.SUMMARY).contains("backend developer"));
        assertTrue(result.get(ResumeSection.SKILLS).contains("java, spring"));
        assertTrue(result.get(ResumeSection.EXPERIENCE).contains("built apis"));
    }
}
