package com.resume.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EducationJson {
    private Long id;
    private String institute;
    private String degree;
    private String duration;
    private String score;
}
