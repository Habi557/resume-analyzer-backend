package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.ResumeAnalysisPollingDto;
import com.resume.backend.entity.JobStatus;
import com.resume.backend.entity.ResumeAnalysisJobEntity;
import com.resume.backend.repository.ResumeAnalysisJobRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PollingServiceImplTest {

    @Test
    void getPolling_returnsMappedJobStatus() {
        ResumeAnalysisJobEntity job = ResumeAnalysisJobEntity.builder()
                .jobId("job-123")
                .status(JobStatus.RUNNING)
                .totalResumes(10)
                .processedResumes(4)
                .failedResumes(1)
                .build();
        PollingServiceImpl service = new PollingServiceImpl(repositoryReturning(Optional.of(job)), new ModelMapper());

        ResumeAnalysisPollingDto result = service.getPolling("job-123");

        assertEquals(JobStatus.RUNNING, result.getStatus());
    }

    @Test
    void getPolling_throwsWhenJobIsMissing() {
        PollingServiceImpl service = new PollingServiceImpl(repositoryReturning(Optional.empty()), new ModelMapper());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.getPolling("missing-job"));

        assertEquals("Job not found", exception.getMessage());
    }

    private ResumeAnalysisJobRepository repositoryReturning(Optional<ResumeAnalysisJobEntity> job) {
        return (ResumeAnalysisJobRepository) Proxy.newProxyInstance(
                ResumeAnalysisJobRepository.class.getClassLoader(),
                new Class<?>[]{ResumeAnalysisJobRepository.class},
                (proxy, method, args) -> {
                    if ("findByJobId".equals(method.getName())) {
                        return job;
                    }
                    if ("toString".equals(method.getName())) {
                        return "ResumeAnalysisJobRepository test proxy";
                    }
                    throw new UnsupportedOperationException("Unexpected method call: " + method.getName());
                }
        );
    }
}
