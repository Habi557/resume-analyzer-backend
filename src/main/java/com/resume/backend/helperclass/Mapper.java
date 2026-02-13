package com.resume.backend.helperclass;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Mapper {
    private final ModelMapper modelMpper;

    Mapper(ModelMapper modelMapper){
        this.modelMpper = modelMapper;
    }
}
