package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.create.PositionCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.service.EmployeeService;
import com.example.employeemanagementsystem.service.PositionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/positions")
@Tag(name = "Position Controller", description = "API для управления должностями")
public class PositionController {

    private final PositionService positionService;
    private final EmployeeService employeeService;

    @Autowired
    public PositionController(PositionService positionService, EmployeeService employeeService) {
        this.positionService = positionService;
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить должность по ID",
        description = "Возвращает должность по указанному ID")
    @ApiResponse(responseCode = "200", description = "Должность найдена")
    @ApiResponse(responseCode = "404", description = "Должность не найдена")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable Long id) {
        PositionDto positionDto = positionService.getPositionById(id);
        if (positionDto == null) {
            throw new ResourceNotFoundException("Position not found with id " + id);
        }
        return ResponseEntity.ok(positionDto);
    }

    @GetMapping
    @Operation(summary = "Получить все должности",
        description = "Возвращает список всех должностей")
    @ApiResponse(responseCode = "200", description = "Список должностей успешно получен")
    public ResponseEntity<List<PositionDto>> getAllPositions() {
        List<PositionDto> positions = positionService.getAllPositions();
        return ResponseEntity.ok(positions);
    }

    @PostMapping
    @Operation(summary = "Создать должность", description = "Создает новую должность")
    @ApiResponse(responseCode = "201", description = "Должность успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<PositionDto> createPosition(@Valid @RequestBody
                                                          PositionCreateDto positionCreateDto) {
        PositionDto createdPosition = positionService.createPosition(positionCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPosition);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить должность",
        description = "Обновляет существующую должность по ID")
    @ApiResponse(responseCode = "200", description = "Должность успешно обновлена")
    @ApiResponse(responseCode = "404", description = "Должность не найдена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<PositionDto> updatePosition(
        @PathVariable Long id, @Valid @RequestBody PositionCreateDto positionCreateDto) {
        PositionDto updatedPosition = positionService.updatePosition(id, positionCreateDto);
        if (updatedPosition == null) {
            throw new ResourceNotFoundException("Position not found with id " + id);
        }
        return ResponseEntity.ok(updatedPosition);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить должность", description = "Удаляет должность по ID")
    @ApiResponse(responseCode = "204", description = "Должность успешно удалена")
    @ApiResponse(responseCode = "404", description = "Должность не найдена")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{positionId}/employees")
    @Operation(summary = "Получить сотрудников по должности",
        description = "Возвращает список сотрудников по ID должности")
    @ApiResponse(responseCode = "200", description = "Список сотрудников успешно получен")
    @ApiResponse(responseCode = "404", description = "Должность не найдена")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByPosition(
        @PathVariable Long positionId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByPositionId(positionId);
        return ResponseEntity.ok(employees);
    }
}