package com.resume.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeProjectionDto {
    private Long id;
    private String originalFileName;
    private String name;
}
