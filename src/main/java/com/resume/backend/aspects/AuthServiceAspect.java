package com.resume.backend.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class AuthServiceAspect {
    @After("execution(* com.resume.backend.serviceImplementation.AuthServiceImpl.*(..))")
    public void authServiceMethods(JoinPoint joinPoint) {
        log.info("Auth service method called {}", joinPoint.getSignature().getName());

    }
    @Around("execution(* com.resume.backend.serviceImplementation.AuthServiceImpl.*(..))")
    public  Object timeCalulatingForMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("Time taken to perform the acton {}",endTime-startTime);
        log.info("Just for testing {}",proceedingJoinPoint.getStaticPart());
        return proceed;

    }
}
