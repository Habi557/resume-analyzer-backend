package com.resume.backend.helperclass;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProblemFactoryTest {

    private final ProblemFactory problemFactory = new ProblemFactory();

    @Test
    void badRequest_buildsBadRequestProblemDetail() {
        ProblemDetail result = problemFactory.badRequest("INVALID_INPUT", "Invalid input", "Name is required");

        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getStatus());
        assertEquals("Invalid input", result.getTitle());
        assertEquals("Name is required", result.getDetail());
        assertEquals("INVALID_INPUT", result.getProperties().get("code"));
        assertNotNull(result.getProperties().get("timestamp"));
    }

    @Test
    void internal_buildsInternalServerErrorProblemDetail() {
        ProblemDetail result = problemFactory.internal("SERVER_ERROR", "Server error");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), result.getStatus());
        assertEquals("Server error", result.getTitle());
        assertEquals("Unexpected error", result.getDetail());
        assertEquals("SERVER_ERROR", result.getProperties().get("code"));
        assertNotNull(result.getProperties().get("timestamp"));
    }

    @Test
    void customResponse_buildsApiResponse() {
        ApiResponse result = problemFactory.customResponse(true, "Done");

        assertTrue(result.isSuccess());
        assertEquals("Done", result.getMessage());
    }
}
