package com.petroandrushchak.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired BrowserProcessHelper browserProcessHelper;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 */10 * ? * *")
    public void cleanUpScheduledTasks() {
        log.debug("Deleting Completed Tasks at {}", DATE_FORMAT.format(new Date()));
        browserProcessHelper.deleteCompletedTasksFromMemory();
    }
}