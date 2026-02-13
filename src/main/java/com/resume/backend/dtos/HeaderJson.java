package com.resume.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeaderJson {
    private String name;
    private String email;
    private String phone;
    private String city;
    private double yearsOfExperience;
}

