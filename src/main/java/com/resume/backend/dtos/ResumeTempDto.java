package com.resume.backend.dtos;

import com.resume.backend.entity.Skill;
import lombok.Data;

import java.util.List;
@Data
public class ResumeTempDto {
    private String name;
    private String email;
    private String phone;
    private String address;
    private List<String> skills;
    private String education;
    private double yearsOfExperience;
    private List<String> redFlags;
}
