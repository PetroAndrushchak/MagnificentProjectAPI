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
        log.debug("Adding Task with Id: " + taskId);
        log.debug("Size of running tasks: " + runningTasks.size());
        runningTasks.put(taskId, task);
    }

    public void removeTask(Long taskId) {
        log.debug("Removing Task with Id: " + taskId);
        log.debug("Size of running tasks: " + runningTasks.size());
        runningTasks.remove(taskId);
    }

    public void deleteCompletedTasksFromMemory() {
        log.debug("Deleting Completed Tasks");
        log.debug("Size of running tasks: " + runningTasks.size());
        runningTasks.entrySet().removeIf(entry -> {
            if (entry.getValue().isDone()) {
                log.info("Task with Id: " + entry.getKey() + " is done");
                return true;
            }
            return false;
        });
    }

    public boolean isTaskRunning(Long taskId) {
        log.debug("Checking if Task with Id: " + taskId + " is running");
        log.debug("Size of running tasks: " + runningTasks.size());
        return runningTasks.containsKey(taskId) && !runningTasks.get(taskId).isDone();
    }

    public void cancelRunningTask(Long taskId) {
        log.debug("Cancelling Task with Id: " + taskId);
        log.debug("Size of running tasks: " + runningTasks.size());

        runningTasks.computeIfAbsent(taskId, id -> {
            throw new BrowserProcessNotFound(id);
        });

        runningTasks.get(taskId).cancel(true);
        if (runningTasks.get(taskId).isCancelled()) {
            log.debug("Task with Id: " + taskId + " is cancelled");
        } else {
            log.debug("Task with Id: " + taskId + " is not cancelled");
        }

        //Add wait for task to be cancelled
    }
}
