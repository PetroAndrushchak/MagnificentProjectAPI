package com.petroandrushchak.aop;

import com.petroandrushchak.helper.RandomHelper;
import com.petroandrushchak.helper.Waiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Aspect
@Component
public class RealPersonAspect {

    @After("@annotation(RealPerson)")
    public void waitRandomTimeBeforeMethodExecution(JoinPoint joinPoint) {
        var randomSecondsToWait = RandomHelper.getRandomNumber(1000, 3000);

        log.info("Waiting for {} seconds before method execution",  randomSecondsToWait / 1000);
        Waiter.waitFor(Duration.ofMillis(randomSecondsToWait));
    }

    @Before("@annotation(RealPerson)")
    public void waitRandomTimeAfterMethodExecution(JoinPoint joinPoint) {
        var randomSecondsToWait = RandomHelper.getRandomNumber(1000, 3000);
        log.info("Waiting for {} seconds after method execution", randomSecondsToWait / 1000);
        Waiter.waitFor(Duration.ofMillis(randomSecondsToWait));
    }


}
