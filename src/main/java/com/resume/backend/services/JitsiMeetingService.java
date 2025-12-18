package com.resume.backend.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JitsiMeetingService {

    public String generateMeetingLink(String interviewer, String candidate) {
        // Clean names (Jitsi doesn't allow spaces)
        interviewer = interviewer.replaceAll("\\s+", "");
        candidate = candidate.replaceAll("\\s+", "");

        // Unique meeting id
        String uniqueId = System.currentTimeMillis() + "-" + UUID.randomUUID();

        String roomName = "Interview-" + interviewer + "-" + candidate + "-" + uniqueId;

        return "https://meet.jit.si/" + roomName;
    }
}

