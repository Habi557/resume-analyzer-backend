package com.resume.backend.serviceImplementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resume.backend.entity.JobStatus;
import com.resume.backend.entity.ResumeAnalysisJobEntity;
import com.resume.backend.repository.ResumeAnalysisJobRepository;
import com.resume.backend.repository.ResumeRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResumeAnalysisServiceImplTest {

    @Test
    void analysisResumeWithJd_createsPendingJobAndStartsAsyncAnalysis() {
        AtomicReference<ResumeAnalysisJobEntity> savedJob = new AtomicReference<>();
        ResumeAnalysisJobRepository jobRepository = jobRepositorySaving(savedJob);
        ResumeRepository resumeRepository = repositoryWithCount(4L);
        FakeResumeAsyncAnalysis asyncAnalysis = new FakeResumeAsyncAnalysis();
        ResumeAnalysisServiceImpl service = new ResumeAnalysisServiceImpl(jobRepository, asyncAnalysis, resumeRepository);

        String jobId = service.analysisResumeWithJd("Java backend role", true);

        assertNotNull(jobId);
        assertFalse(jobId.isBlank());
        assertEquals(jobId, savedJob.get().getJobId());
        assertEquals("Java backend role", savedJob.get().getJobRole());
        assertEquals(JobStatus.PENDING, savedJob.get().getStatus());
        assertEquals(4, savedJob.get().getTotalResumes());
        assertEquals(0, savedJob.get().getProcessedResumes());
        assertEquals(0, savedJob.get().getFailedResumes());
        assertNotNull(savedJob.get().getCreatedAt());
        assertTrue(asyncAnalysis.called.get());
        assertEquals("Java backend role", asyncAnalysis.jobRole.get());
        assertEquals(jobId, asyncAnalysis.jobId.get());
        assertTrue(asyncAnalysis.scanAll.get());
    }

    private ResumeAnalysisJobRepository jobRepositorySaving(AtomicReference<ResumeAnalysisJobEntity> savedJob) {
        return (ResumeAnalysisJobRepository) Proxy.newProxyInstance(
                ResumeAnalysisJobRepository.class.getClassLoader(),
                new Class<?>[]{ResumeAnalysisJobRepository.class},
                (proxy, method, args) -> {
                    if ("save".equals(method.getName())) {
                        savedJob.set((ResumeAnalysisJobEntity) args[0]);
                        return args[0];
                    }
                    if ("toString".equals(method.getName())) {
                        return "ResumeAnalysisJobRepository test proxy";
                    }
                    throw new UnsupportedOperationException("Unexpected method call: " + method.getName());
                }
        );
    }

    private ResumeRepository repositoryWithCount(long count) {
        return (ResumeRepository) Proxy.newProxyInstance(
                ResumeRepository.class.getClassLoader(),
                new Class<?>[]{ResumeRepository.class},
                (proxy, method, args) -> {
                    if ("count".equals(method.getName())) {
                        return count;
                    }
                    if ("toString".equals(method.getName())) {
                        return "ResumeRepository test proxy";
                    }
                    throw new UnsupportedOperationException("Unexpected method call: " + method.getName());
                }
        );
    }

    private static class FakeResumeAsyncAnalysis extends ResumeAsyncAnalysis {
        private final AtomicBoolean called = new AtomicBoolean();
        private final AtomicReference<String> jobRole = new AtomicReference<>();
        private final AtomicReference<String> jobId = new AtomicReference<>();
        private final AtomicBoolean scanAll = new AtomicBoolean();

        FakeResumeAsyncAnalysis() {
            super(null, null, null, null, null, null, null, new ObjectMapper(), null, null, null);
        }

        @Override
        public void test(String jobRole, String jobId, boolean scanAllresumesIsChecked) {
            called.set(true);
            this.jobRole.set(jobRole);
            this.jobId.set(jobId);
            this.scanAll.set(scanAllresumesIsChecked);
        }
    }
}
