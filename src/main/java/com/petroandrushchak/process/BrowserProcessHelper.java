package com.petroandrushchak.process;

import com.petroandrushchak.exceptions.BrowserProcessNotFound;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Slf4j
public class BrowserProcessHelper {

    private final Map<Long, Future<?>> runningTasks = new ConcurrentHashMap<>();

    public void addTask(Long taskId, Future<?> task) {
        log.info("Adding Task with Id: " + taskId);
        log.info("Size of running tasks: " + runningTasks.size());
        runningTasks.put(taskId, task);
    }

    public void removeTask(Long taskId) {
        log.info("Removing Task with Id: " + taskId);
        log.info("Size of running tasks: " + runningTasks.size());
        runningTasks.remove(taskId);
    }

    public void deleteCompletedTasksFromMemory() {
        log.info("Deleting Completed Tasks");
        log.info("Size of running tasks: " + runningTasks.size());
        runningTasks.entrySet().removeIf(entry -> {
            if (entry.getValue().isDone()) {
                log.info("Task with Id: " + entry.getKey() + " is done");
                return true;
            }
            return false;
        });
    }

    public boolean isTaskRunning(Long taskId) {
        log.info("Checking if Task with Id: " + taskId + " is running");
        log.info("Size of running tasks: " + runningTasks.size());
        return runningTasks.containsKey(taskId) && !runningTasks.get(taskId).isDone();
    }

    public void cancelRunningTask(Long taskId) {
        log.info("Cancelling Task with Id: " + taskId);
        log.info("Size of running tasks: " + runningTasks.size());

        runningTasks.computeIfAbsent(taskId, id -> {
            throw new BrowserProcessNotFound(id);
        });

        runningTasks.get(taskId).cancel(true);
        if (runningTasks.get(taskId).isCancelled()) {
            log.info("Task with Id: " + taskId + " is cancelled");
        } else {
            log.info("Task with Id: " + taskId + " is not cancelled");
        }

        //Add wait for task to be cancelled
    }
}
