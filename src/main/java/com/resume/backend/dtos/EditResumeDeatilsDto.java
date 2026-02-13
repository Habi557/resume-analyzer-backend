package com.resume.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditResumeDeatilsDto {
    private Long id;
    private String name;
    private String email;
    private String address;
    private EducationJson[] education;
    private double yearsOfExperience;

}
