package com.resume.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class ProjectJson {
    private String title;
    private String duration;
    private List<String> description;
}

