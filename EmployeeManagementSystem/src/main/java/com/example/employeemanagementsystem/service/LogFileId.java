package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.model.LogFileTask;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LogFileId {

    private final Map<String, LogFileTask> tasks = new ConcurrentHashMap<>();
    private final LogFileGenerator generatorService;

    @Autowired
    public LogFileId(LogFileGenerator generatorService) {
        this.generatorService = generatorService;
    }

    public String createLogFileTask(LocalDate date) {
        String taskId = UUID.randomUUID().toString();
        LogFileTask task = new LogFileTask(taskId);
        tasks.put(taskId, task);
        generatorService.generateLogFileAsync(task, date);
        return taskId;
    }

    public LogFileTask getTaskStatus(String taskId) {
        return tasks.get(taskId);
    }

    public Path getLogFilePath(String taskId) {
        LogFileTask task = tasks.get(taskId);
        if (task != null && "COMPLETED".equals(task.getStatus())) {
            return task.getFilePath();
        }
        return null;
    }
}