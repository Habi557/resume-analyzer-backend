package com.resume.backend.serviceImplementation;
import com.resume.backend.dtos.FileDownloadDataDto;
import com.resume.backend.entity.Resume;
import com.resume.backend.repository.ResumeRepository;
import com.resume.backend.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
public class S3StorageService implements StorageService {

    @Value("${aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;
    private final ResumeRepository resumeRepository;

    public S3StorageService(ResumeRepository resumeRepository) {
        this.s3Client = S3Client.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        this.resumeRepository=resumeRepository;
    }

    @Override
    public String saveFile(MultipartFile file, String username) throws IOException {
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String s3Key = "resumes/" + username + "/" + uniqueFileName;

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .contentType(file.getContentType())
                        .acl(ObjectCannedACL.PRIVATE)
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );

        return s3Key; // return S3 key, not path
    }

    @Override
    public InputStream loadFile(String key) throws IOException {
        ResponseBytes<GetObjectResponse> object =
                s3Client.getObjectAsBytes(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());

        return new ByteArrayInputStream(object.asByteArray());
    }

    @Override
    public FileDownloadDataDto downloadResume(long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(resume.getFilePath())
                .build();

        ResponseInputStream<GetObjectResponse> s3Stream =
                s3Client.getObject(request);

        GetObjectResponse response = s3Stream.response();

        Resource resource = new InputStreamResource(s3Stream);

        FileDownloadDataDto data = new FileDownloadDataDto();
        data.setResource(resource);
        data.setContentType(response.contentType());
        data.setFileSize(response.contentLength());
        data.setFileName(resume.getOriginalFileName());

        return data;
    }
}

