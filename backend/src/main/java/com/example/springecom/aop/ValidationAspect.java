package com.example.springecom.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ValidationAspect {
    private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ValidationAspect.class);

    @Around("execution(* com.example.springecom.service.ProductService.getProductById(..)) && args(postId)")
    public Object validateAndUpdate(ProceedingJoinPoint jp, int postId) throws Throwable{
        if(postId < 0){
            LOGGER.info("Negative postId {} is not valid. Converting to positive.", postId);
            postId = -postId;
        }
        Object result = jp.proceed(new Object[]{postId});
        return result;
    }
}
