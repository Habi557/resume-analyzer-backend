package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.FileDownloadDataDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.File;


@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
public class LocalStorageService implements StorageService {

    @Value("${upload.dir}")
    private String uploadDir;
    @Autowired
    private ResumeRepository resumeRepository;

    @Override
    public String saveFile(MultipartFile file, String username) throws IOException {
        File directory = new File(uploadDir + "/" + username);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = directory.getAbsolutePath() + "/" + uniqueFileName;

        file.transferTo(new File(filePath));

        return filePath; // return local path
    }



    @Override
    public InputStream loadFile(String path) throws IOException {
        return new FileInputStream(path);
    }

    @Override
    public FileDownloadDataDto downloadResume(long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        Path path = Paths.get(resume.getFilePath());
        Resource resource = new FileSystemResource(path);

        if (!resource.exists()) {
            throw new RuntimeException("File not found");
        }

        String contentType;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        FileDownloadDataDto data = new FileDownloadDataDto();
        data.setResource(resource);
        data.setContentType(contentType);
        data.setFileName(resume.getOriginalFileName());
        data.setFileSize(path.toFile().length());

        return data;
    }
}

