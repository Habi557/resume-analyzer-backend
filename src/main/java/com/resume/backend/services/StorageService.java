package com.resume.backend.services;

import com.resume.backend.dtos.FileDownloadDataDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {
    String saveFile(MultipartFile file, String username) throws IOException;
    InputStream loadFile(String path) throws IOException;
    FileDownloadDataDto downloadResume(long resumeId);

}

