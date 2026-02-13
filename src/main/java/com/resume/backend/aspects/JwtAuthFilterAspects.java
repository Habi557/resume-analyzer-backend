//package com.resume.backend.aspects;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//@Aspect
//public class JwtAuthFilterAspects {
//    @Pointcut("execution(* com.resume.backend.configurations.JwtAuthFilter.*(..))")
//    public void  jwtAuthFilterMethods(){}
//    @Before("jwtAuthFilterMethods()")
//    public void logBefor(JoinPoint joinPoint){
//        log.info("Entering the method {}",joinPoint.getSignature().getName());
//    }
//    @AfterReturning("jwtAuthFilterMethods()")
//    public void logAfter(JoinPoint joinPoint){
//        log.info("Exiting method{}",joinPoint.getSignature().getName());
//    }
//    @AfterThrowing(pointcut = "jwtAuthFilterMethods()",throwing = "ex")
//    public void  logException(JoinPoint joinPoint, Exception ex){
//        log.error("Exception in method {} : {}",
//                joinPoint.getSignature().getName(),
//                ex.getMessage());    }
//
//}
