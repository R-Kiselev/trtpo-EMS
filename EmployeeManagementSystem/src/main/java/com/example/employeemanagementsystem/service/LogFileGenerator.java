package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.model.LogFileTask;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
public class LogFileGenerator {

    private static final Logger logger = LoggerFactory.getLogger(LogFileGenerator.class);
    private static final String LOG_FILE_PATTERN = "logs/employee-management-%s.log";
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Async
    public void generateLogFileAsync(LogFileTask task, LocalDate date) {
        logger.info("Starting log file generation for task {} in thread {}",
            task.getTaskId(), Thread.currentThread().getName());
        try {
            String logFileName = String.format(LOG_FILE_PATTERN, date.format(DATE_FORMATTER));
            Path logFilePath = Paths.get(logFileName).normalize();

            if (!Files.exists(logFilePath)) {
                task.setStatus("FAILED");
                task.setErrorMessage("Log file for date " + date + " not found");
                return;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            List<String> lines = Files.readAllLines(logFilePath, StandardCharsets.UTF_8);
            Path outputPath = Paths.get("logs/task-" + task.getTaskId() + "-" + date + ".log");
            Files.write(outputPath, lines, StandardCharsets.UTF_8);

            task.setFilePath(outputPath);
            task.setStatus("COMPLETED");
            logger.info("Log file generation completed for task {}", task.getTaskId());
        } catch (IOException e) {
            logger.error("Error generating log file for task {}: {}",
                task.getTaskId(), e.getMessage());
            task.setStatus("FAILED");
            task.setErrorMessage("Failed to generate log file: " + e.getMessage());
        }
    }
}