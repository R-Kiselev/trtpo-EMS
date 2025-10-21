package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.model.LogFileTask;
import com.example.employeemanagementsystem.service.LogFileId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log Controller", description = "API для работы с лог-файлами")
public class LogsController {

    private final LogFileId logFileId;
    private static final String ARCHIVE_LOG_FILE_PATTERN = "logs/employee-management-%s.log";
    private static final DateTimeFormatter DATE_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    public LogsController(LogFileId logFileId) {
        this.logFileId = logFileId;
    }

    @PostMapping("/generate")
    @Operation(summary = "Создать задачу генерации лог-файла асинхронно",
        description = "Создает задачу для генерации лог-файла и возвращает ID задачи.")
    @ApiResponse(responseCode = "202", description = "Задача успешно создана")
    @ApiResponse(responseCode = "400", description = "Неверный формат даты")
    public ResponseEntity<String> createLogFileTask(
        @Parameter(description = "Дата в формате yyyy-MM-dd", required = true, example =
            "2025-04-01")
        @RequestParam String date) {
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return new ResponseEntity<>("Invalid date format", HttpStatus.BAD_REQUEST);
        }

        String taskId = logFileId.createLogFileTask(parsedDate);
        return new ResponseEntity<>(taskId, HttpStatus.ACCEPTED);
    }

    @GetMapping("/status/{taskId}")
    @Operation(summary = "Получить статус задачи",
        description = "Возвращает статус задачи генерации лог-файла по ID.")
    @ApiResponse(responseCode = "200", description = "Статус получен")
    @ApiResponse(responseCode = "404", description = "Задача не найдена")
    public ResponseEntity<LogFileTask> getTaskStatus(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable String taskId) {
        LogFileTask task = logFileId.getTaskStatus(taskId);
        if (task == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @GetMapping("/download/{taskId}")
    @Operation(summary = "Скачать сгенерированный лог-файл",
        description = "Скачивает готовый лог-файл по ID задачи.")
    @ApiResponse(responseCode = "200", description = "Файл успешно загружен")
    @ApiResponse(responseCode = "404", description = "Файл не найден")
    @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    public ResponseEntity<Resource> downloadLogFile(
        @Parameter(description = "ID задачи", required = true)
        @PathVariable String taskId) {
        Path filePath = logFileId.getLogFilePath(taskId);
        if (filePath == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="
                    + filePath.getFileName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download")
    @Operation(summary = "Скачать лог-файл по дате", description =
        "Скачивает логи за указанную дату.")
    @ApiResponse(responseCode = "200", description = "Логи успешно загружены")
    @ApiResponse(responseCode = "404", description = "Логи не найдены")
    @ApiResponse(responseCode = "400", description = "Неверный формат даты")
    public ResponseEntity<Resource> downloadLogFileByDate(
        @Parameter(description = "Дата в формате yyyy-MM-dd", required = true, example =
            "2025-04-01")
        @RequestParam(name = "date") String dateStr) throws IOException {
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        Path logPath = getLogFilePath(date);
        if (!Files.exists(logPath)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(logPath.toUri());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="
                + logPath.getFileName())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }

    @GetMapping("/view")
    @Operation(summary = "Просмотреть логи", description =
        "Возвращает логи за указанную дату в виде текста.")
    @ApiResponse(responseCode = "200", description = "Логи успешно получены")
    @ApiResponse(responseCode = "404", description = "Логи не найдены")
    @ApiResponse(responseCode = "400", description = "Неверный формат даты")
    public ResponseEntity<String> viewLogFile(
        @Parameter(description = "Дата в формате yyyy-MM-dd", required = true, example =
            "2025-04-01")
        @RequestParam(name = "date") String dateStr) throws IOException {
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().build();
        }
        Path logPath = getLogFilePath(date);
        if (!Files.exists(logPath)) {
            return ResponseEntity.notFound().build();
        }
        String logContent = Files.readString(logPath);

        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_PLAIN)
            .body(logContent);
    }

    private Path getLogFilePath(LocalDate date) {
        String fileName = String.format(ARCHIVE_LOG_FILE_PATTERN, date.format(DATE_FORMATTER));
        return Path.of(fileName);
    }
}