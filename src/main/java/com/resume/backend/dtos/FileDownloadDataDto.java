package com.resume.backend.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;


@Getter
@Setter
public class FileDownloadDataDto {

    private Resource resource;
    private String contentType;
    private String fileName;
    private long fileSize;

}

