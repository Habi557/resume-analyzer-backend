package com.resume.backend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AiResumeTempDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private List<String> skills;
    private String education;
    private double yearsOfExperience;
    private List<String> redFlags;
}
