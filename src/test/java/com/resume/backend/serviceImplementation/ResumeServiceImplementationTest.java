package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.FileDownloadDataDto;
import com.resume.backend.services.StorageService;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ResumeServiceImplementationTest {

    @Test
    void dowloadResume_delegatesToStorageService() {
        FileDownloadDataDto downloadData = new FileDownloadDataDto();
        AtomicLong receivedResumeId = new AtomicLong();
        StorageService storageService = new StorageService() {
            @Override
            public String saveFile(MultipartFile file, String username) throws IOException {
                throw new UnsupportedOperationException("Not needed for this test");
            }

            @Override
            public InputStream loadFile(String path) throws IOException {
                throw new UnsupportedOperationException("Not needed for this test");
            }

            @Override
            public FileDownloadDataDto downloadResume(long resumeId) {
                receivedResumeId.set(resumeId);
                return downloadData;
            }
        };
        ResumeServiceImplementation service = new ResumeServiceImplementation(
                null, null, null, null, null, null, null, storageService, null
        );

        FileDownloadDataDto result = service.dowloadResume(99L);

        assertEquals(99L, receivedResumeId.get());
        assertSame(downloadData, result);
    }
}
