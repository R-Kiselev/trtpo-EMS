package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.create.DepartmentCreateDto;
import com.example.employeemanagementsystem.dto.get.DepartmentDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.service.DepartmentService;
import com.example.employeemanagementsystem.service.EmployeeService;
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
@RequestMapping("/api/departments")
@Tag(name = "Department Controller", description = "API для управления отделами")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final EmployeeService employeeService;

    @Autowired
    public DepartmentController(DepartmentService departmentService,
                                EmployeeService employeeService) {
        this.departmentService = departmentService;
        this.employeeService = employeeService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить отдел по ID", description = "Возвращает отдел по указанному ID")
    @ApiResponse(responseCode = "200", description = "Отдел найден")
    @ApiResponse(responseCode = "404", description = "Отдел не найден")
    public ResponseEntity<DepartmentDto> getDepartmentById(@Valid @PathVariable Long id) {
        DepartmentDto departmentDto = departmentService.getDepartmentById(id);
        if (departmentDto == null) {
            throw new ResourceNotFoundException("Department not found with id " + id);
        }
        return ResponseEntity.ok(departmentDto);
    }

    @GetMapping
    @Operation(summary = "Получить все отделы", description = "Возвращает список всех отделов")
    @ApiResponse(responseCode = "200", description = "Список отделов успешно получен")
    public ResponseEntity<List<DepartmentDto>> getAllDepartments() {
        List<DepartmentDto> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @PostMapping
    @Operation(summary = "Создать отдел", description = "Создает новый отдел")
    @ApiResponse(responseCode = "201", description = "Отдел успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<DepartmentDto> createDepartment(
        @Valid @RequestBody DepartmentCreateDto departmentDto) {
        DepartmentDto createdDepartment = departmentService.createDepartment(departmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить отдел", description = "Обновляет существующий отдел по ID")
    @ApiResponse(responseCode = "200", description = "Отдел успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Отдел не найден")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<DepartmentDto> updateDepartment(
        @PathVariable Long id,
        @Valid @RequestBody DepartmentCreateDto departmentDetails) {
        DepartmentDto updatedDepartment = departmentService.updateDepartment(id, departmentDetails);
        if (updatedDepartment == null) {
            throw new ResourceNotFoundException("Department not found with id " + id);
        }
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить отдел", description = "Удаляет отдел по ID")
    @ApiResponse(responseCode = "204", description = "Отдел успешно удален")
    @ApiResponse(responseCode = "404", description = "Отдел не найден")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{departmentId}/employees")
    @Operation(summary = "Получить сотрудников отдела",
        description = "Возвращает список сотрудников по ID отдела")
    @ApiResponse(responseCode = "200", description = "Список сотрудников успешно получен")
    @ApiResponse(responseCode = "404", description = "Отдел не найден")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(
        @PathVariable Long departmentId) {
        List<EmployeeDto> employees = employeeService.getEmployeesByDepartmentId(departmentId);
        return ResponseEntity.ok(employees);
    }
}