package com.resume.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class ResumeAiBackendApplication{


	public static void main(String[] args) {
		SpringApplication.run(ResumeAiBackendApplication.class, args);

    }
	
}

