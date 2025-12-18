package com.resume.backend.dtos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDto {
    private Long id;
    private String userName;
    private String password;
    private String email;
    private List<ResumeTempDto> resumeTempDtoList = new ArrayList<>();
    private Set<RolesDto> rolesDtoSet = new HashSet<>();
}
