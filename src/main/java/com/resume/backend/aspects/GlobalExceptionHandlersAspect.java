package com.resume.backend.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class GlobalExceptionHandlersAspect {
    @After("execution(* com.resume.backend.globalexceptions.*.*(..))")
    public void authServiceMethods(JoinPoint joinPoint) {
        log.info("Auth service method called {}", joinPoint.getSignature().getName());

    }
}
