package com.resume.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class ExperienceJson {
    private String company;
    private String role;
    private String startDate;
    private String endDate;
    private List<String> responsibilities;
}

