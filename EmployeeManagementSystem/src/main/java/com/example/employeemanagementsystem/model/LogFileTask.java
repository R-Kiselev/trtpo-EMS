package com.example.employeemanagementsystem.model;

import java.nio.file.Path;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogFileTask {
    private String taskId;
    private String status = "PENDING";
    private Path filePath;
    private String errorMessage;

    // Добавляем конструктор, который принимает только taskId
    public LogFileTask(String taskId) {
        this.taskId = taskId;
    }
}