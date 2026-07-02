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
        //String str ="How are you and how you are";
       // Stream<String> stream = Stream.of(str.split(" "));
       // Map<String, Long> collect = stream.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<Integer> integers = List.of(10, 15, 18, 11, 6, 35,10,15);
        Map<Integer, Long> collect = integers.stream().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        System.out.println(collect);
        String str ="programming";
        Stream<String> split = Stream.of(str.split(""));
        Map<String, Long> collect1 = split.collect(Collectors.groupingBy(c -> c, Collectors.counting()));
        System.out.println(collect1);
        KeyValues listOfEmployees;
        listOfEmployees.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.maxBy(Comparator.comparingInt(Employee::getSalary))));


    }
	
}

