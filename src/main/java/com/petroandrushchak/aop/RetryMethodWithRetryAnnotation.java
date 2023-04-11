package com.petroandrushchak.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class RetryMethodWithRetryAnnotation {

    private static final ThreadLocal<Boolean> processingWrapper = ThreadLocal.withInitial(() -> false);

    public static Boolean isProcessing() {
        return processingWrapper.get();
    }

    @Around("@annotation(retryStep)")
    public Object handleRetries(final ProceedingJoinPoint joinPoint, RetryStep retryStep) throws Throwable {
        log.info("Retrying with Retry Step Annotation");
        processingWrapper.set(true);
        int retryCount = retryStep.value();
        Throwable storedException = null;
        Object result = null;
        boolean processed = false;
        int i = 0;
        while (!processed && i < retryCount) {
            try {
                result = joinPoint.proceed();
                processed = true;
            } catch (Throwable throwable) {
                log.warn("Retry # " + i + ":\r\n" + throwable);
                storedException = throwable;
            }
            i++;
        }

        processingWrapper.set(false);

        if (!processed) {
            assert storedException != null;
            throw storedException;
        }

        return result;
    }


}