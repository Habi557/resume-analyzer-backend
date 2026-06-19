package com.resume.backend.serviceImplementation;

import com.resume.backend.dtos.DashboardDto;
import com.resume.backend.projection.DashboardProjection;
import com.resume.backend.repository.ResumeRepository;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardDeatilsImplTest {

    @Test
    void getAllDashboardDetails_mapsProjectionToDto() {
        AtomicInteger callCount = new AtomicInteger();
        ResumeRepository resumeRepository = repositoryReturning(callCount, projection(12, 7, 3.5, 91));

        DashboardDeatilsImpl service = new DashboardDeatilsImpl(resumeRepository);

        DashboardDto result = service.getAllDashboardDetails();

        assertEquals(12, result.getTotalResumes());
        assertEquals(7, result.getCanditateScanned());
        assertEquals(3.5, result.getAverageExperience());
        assertEquals(91, result.getBestMatch());
        assertEquals(1, callCount.get());
    }

    @Test
    void getAllDashboardDetails_returnsDefaultDtoWhenProjectionIsNull() {
        AtomicInteger callCount = new AtomicInteger();
        ResumeRepository resumeRepository = repositoryReturning(callCount, () -> null);

        DashboardDeatilsImpl service = new DashboardDeatilsImpl(resumeRepository);

        DashboardDto result = service.getAllDashboardDetails();

        assertEquals(0, result.getTotalResumes());
        assertEquals(0, result.getCanditateScanned());
        assertEquals(0.0, result.getAverageExperience());
        assertEquals(0, result.getBestMatch());
        assertEquals(1, callCount.get());
    }

    @Test
    void getAllDashboardDetails_convertsNullIntegerValuesToZero() {
        AtomicInteger callCount = new AtomicInteger();
        ResumeRepository resumeRepository = repositoryReturning(callCount, projection(null, null, 0.0, null));

        DashboardDeatilsImpl service = new DashboardDeatilsImpl(resumeRepository);

        DashboardDto result = service.getAllDashboardDetails();

        assertEquals(0, result.getTotalResumes());
        assertEquals(0, result.getCanditateScanned());
        assertEquals(0.0, result.getAverageExperience());
        assertEquals(0, result.getBestMatch());
        assertEquals(1, callCount.get());
    }

    private ResumeRepository repositoryReturning(AtomicInteger callCount, Supplier<DashboardProjection> supplier) {
        return (ResumeRepository) Proxy.newProxyInstance(
                ResumeRepository.class.getClassLoader(),
                new Class<?>[]{ResumeRepository.class},
                (proxy, method, args) -> {
                    if ("getDashboardDetails".equals(method.getName())) {
                        callCount.incrementAndGet();
                        return supplier.get();
                    }
                    if ("toString".equals(method.getName())) {
                        return "ResumeRepository test proxy";
                    }
                    throw new UnsupportedOperationException("Unexpected method call: " + method.getName());
                }
        );
    }

    private Supplier<DashboardProjection> projection(Integer totalResumes,
                                                    Integer candidatesScreened,
                                                    Double averageExperience,
                                                    Integer bestMatch) {
        return () -> new DashboardProjection() {
            @Override
            public Integer getTotalResumes() {
                return totalResumes;
            }

            @Override
            public Integer getTotalAnalysed() {
                return null;
            }

            @Override
            public Integer getTotalNotAnalysed() {
                return null;
            }

            @Override
            public Integer getCandidatesScreened() {
                return candidatesScreened;
            }

            @Override
            public Integer getBestMatch() {
                return bestMatch;
            }

            @Override
            public Double getAverageExperience() {
                return averageExperience;
            }
        };
    }
}
