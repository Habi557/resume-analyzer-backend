package com.resume.backend.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String provider;
    private String providerId;
    private List<ResumeTempDto> resumeTempDtoList = new ArrayList<>();
    private Set<RolesDto> rolesDtoSet = new HashSet<>();
}
