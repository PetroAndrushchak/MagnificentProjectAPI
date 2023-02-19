package com.petroandrushchak.helper;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.BooleanSupplier;

import static org.awaitility.Awaitility.await;

@Slf4j
@UtilityClass
public class Waiter {

    public static boolean isConditionTrueAtMostDuringPeriod(Duration duration, BooleanSupplier condition) {
        LocalDateTime futureTime = LocalDateTime.now().plus(duration);
        log.info("Checking if condition is true,  Timeout " + duration.toSeconds());
        while (LocalDateTime.now().isBefore(futureTime)) {
            if (condition.getAsBoolean()) {
                log.info("Condition result is true");
                return true;
            } else {
                log.info("Condition result is false");
                Waiter.waitFor(Duration.ofMillis(500));
            }
        }
        return false;
    }

    public static boolean isConditionAlwaysTrueDuringPeriod(Duration duration, BooleanSupplier condition) {
        LocalDateTime futureTime = LocalDateTime.now().plus(duration);
        log.info("Checking if condition is true during: " + duration.toSeconds() + " seconds");
        while (LocalDateTime.now().isBefore(futureTime)) {
            if (condition.getAsBoolean()) {
                log.info("Condition result is true");
                Waiter.waitFor(Duration.ofMillis(500));
            } else {
                log.info("Condition result is false");
                return false;
            }
        }
        return true;
    }

    public static void waitUntilOneOfConditionsMatchImmediately(Duration duration, BooleanSupplier... conditions) {
        log.info("Wait until one of the condition match immediately");
        await().pollInSameThread()
               .atMost(duration)
               .pollInterval(Duration.ofMillis(100))
               .until(() -> {
                   log.info("Check if one of the condition match");
                   var result = Arrays.stream(conditions).anyMatch(BooleanSupplier::getAsBoolean);
                   log.info("Result: " + result);
                   return result;
               });
    }

    public static void waitUntilOneOfConditionsMatch(Duration duration, BooleanSupplier... conditions) {
        log.info("Wait until one of the condition match");
        await().pollInSameThread()
               .atMost(duration)
               .pollInterval(Duration.ofSeconds(1))
               .until(() -> Arrays.stream(conditions).anyMatch(BooleanSupplier::getAsBoolean));
        log.info("Waiting is finished");
    }

    public static void waitUntilConditionMatchDuringPeriod(Duration totalWaitTime, Duration conditionDuration, BooleanSupplier condition) {
        log.info("Wait until condition is true during: " + conditionDuration.toSeconds() + " seconds, Total wait time: " + totalWaitTime.toSeconds() + " seconds");
        await().pollInSameThread()
               .atMost(totalWaitTime)
               .pollInterval(Duration.ofMillis(100))
               .until(() -> isConditionAlwaysTrueDuringPeriod(conditionDuration, condition));
    }

    public static void waitForOneSecond() {
        waitFor(Duration.ofSeconds(1));
    }

    @SneakyThrows
    public static void waitFor(Duration duration) {
        Thread.sleep(duration.toMillis());
    }

}
