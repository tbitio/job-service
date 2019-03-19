package com.easy.jobservice.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class AppLogging {

    @AfterThrowing(
            pointcut = "execution(* com.easy.jobservice..*ServiceImpl.*(..))",
            throwing = "error")
    public void logServiceAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.warn(
                String.format(
                        "%s. Exception: %s",
                        joinPoint.getSignature(),
                        error
                )
        );
    }
}
