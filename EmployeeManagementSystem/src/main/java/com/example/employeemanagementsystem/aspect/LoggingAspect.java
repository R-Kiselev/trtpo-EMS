package com.example.employeemanagementsystem.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    @Before("execution(* com.example.employeemanagementsystem.controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Executing method: {}", joinPoint.getSignature().toShortString());
        }
    }

    
    @AfterReturning(pointcut =
        "execution(* com.example.employeemanagementsystem.controller.*.*(..))",
        returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            logger.info("Method {} executed successfully. Result: {}",
                joinPoint.getSignature().toShortString(), result);
        }
    }

    
    @AfterThrowing(pointcut = 
        "execution(* com.example.employeemanagementsystem.controller.*.*(..))",
        throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        if (logger.isErrorEnabled()) {
            logger.error("Error in method: {}. Error: {}", 
                joinPoint.getSignature().toShortString(), error.getMessage(), error); 
        }
    }
}