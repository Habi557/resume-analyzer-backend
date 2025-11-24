package com.resume.backend;

import com.resume.backend.dao.AppDAo;
import com.resume.backend.entity.Course;
import com.resume.backend.entity.Instructor;
import com.resume.backend.entity.InstructorDetails;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@EnableAsync
@SpringBootApplication
public class ResumeAiBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(ResumeAiBackendApplication.class, args);
	}
	
}

