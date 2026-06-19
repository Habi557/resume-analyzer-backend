package com.resume.backend.serviceImplementation;

import com.resume.backend.repository.ResumeRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResumeDeletionImplTest {

    @Test
    void deleteResume_deletesByIdAndReturnsMessage() {
        AtomicReference<Long> deletedId = new AtomicReference<>();
        ResumeRepository resumeRepository = repositoryDeleting(deletedId);
        ResumeDeletionImpl service = new ResumeDeletionImpl(resumeRepository);

        String result = service.deleteResume(15L);

        assertEquals(15L, deletedId.get());
        assertEquals("Resume Deleted Sucessfully", result);
    }

    private ResumeRepository repositoryDeleting(AtomicReference<Long> deletedId) {
        return (ResumeRepository) Proxy.newProxyInstance(
                ResumeRepository.class.getClassLoader(),
                new Class<?>[]{ResumeRepository.class},
                (proxy, method, args) -> {
                    if ("deleteById".equals(method.getName())) {
                        deletedId.set((Long) args[0]);
                        return null;
                    }
                    if ("toString".equals(method.getName())) {
                        return "ResumeRepository test proxy";
                    }
                    throw new UnsupportedOperationException("Unexpected method call: " + method.getName());
                }
        );
    }
}
