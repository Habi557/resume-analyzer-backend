package com.resume.backend.dtos;

import com.resume.backend.entity.Skill;
import lombok.*;

import java.util.List;
@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeTempDto {
    private Long id;
    private  UserDto userDto;
    private String name;
    private String email;
    private String phone;
    private String address;
    //private List<String> skills;
    private List<SkillDto> skills;
    private List<EducationJson> education;
    private double yearsOfExperience;
    private List<String> redFlags;
}
