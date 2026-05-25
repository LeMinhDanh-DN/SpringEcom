package com.example.springecom.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class loggingAspect {
    public static final Logger LOGGER = LoggerFactory.getLogger(loggingAspect.class);


    // return type, package, class, method, parameters
    @Before("execution(* com.example.springecom.controller.*.*(..))")
    public void logMethodCall(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        LOGGER.info("Method called: {}", methodName);
    }


    @AfterThrowing(pointcut = "execution(* com.example.springecom.controller.*.*(..))", throwing = "ex")
    public void logMethodException(JoinPoint joinPoint, Throwable ex){
        String methodName = joinPoint.getSignature().getName();
        LOGGER.error("Exception in method: {} with message: {}", methodName, ex.getMessage());;
    }

    @AfterReturning(pointcut = "execution(* com.example.springecom.controller.*.*(..))", returning = "result")
    public void logMethodResult(JoinPoint joinPoint, Object result){
        String methodName = joinPoint.getSignature().getName();
        LOGGER.info("Method {} succeeded", methodName);
    }
}
