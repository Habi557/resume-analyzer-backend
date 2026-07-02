package com.resume.backend;

import com.resume.backend.dtos.DashboardDto;
import io.micrometer.common.KeyValues;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class ResumeAiBackendApplication{


	public static void main(String[] args) {
		SpringApplication.run(ResumeAiBackendApplication.class, args);


    }
	
}

