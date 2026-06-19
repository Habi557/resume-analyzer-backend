package com.resume.backend;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ResumeAiBackendApplicationTests {

	@Test
	void applicationClassCanBeLoaded() {
		assertDoesNotThrow(() -> Class.forName(ResumeAiBackendApplication.class.getName()));
	}

}
