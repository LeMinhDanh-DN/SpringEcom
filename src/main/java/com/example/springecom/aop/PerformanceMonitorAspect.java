package com.example.springecom.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceMonitorAspect {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PerformanceMonitorAspect.class);

    //throw is compusolry
    @Around("execution(* com.example.springecom.service.ProductService.getProductById(..))")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();
        // throw exception if crassed

        long endTime = System.currentTimeMillis();

        LOGGER.info("Method{} executed in {} ms", joinPoint.getSignature().getName(), endTime - startTime);

        return result;
    }
}
