package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@Tag(name = "Employee Controller", description = "API для управления сотрудниками")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeController(EmployeeService employeeService, EmployeeMapper employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить сотрудника по ID",
        description = "Возвращает сотрудника по указанному ID")
    @ApiResponse(responseCode = "200", description = "Сотрудник найден")
    @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        EmployeeDto employeeDto = employeeService.getEmployeeDtoById(id);
        if (employeeDto == null) {
            throw new ResourceNotFoundException("Employee not found with id " + id);
        }
        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping(params = {"departmentId", "positionId"})
    @Operation(summary = "Получить сотрудников по отделу и должности",
        description = "Возвращает список сотрудников по ID отдела и должности")
    @ApiResponse(responseCode = "200", description = "Список сотрудников успешно получен")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartmentAndPosition(
        @RequestParam("departmentId") Long departmentId,
        @RequestParam("positionId") Long positionId) {
        List<EmployeeDto> employees = employeeService
            .getEmployeesByDepartmentIdAndPositionId(departmentId, positionId);
        return ResponseEntity.ok(employees);
    }

    @GetMapping(params = {"roleName"})
    @Operation(summary = "Получить сотрудников по роли",
        description = "Возвращает список сотрудников по имени роли")
    @ApiResponse(responseCode = "200", description = "Список сотрудников успешно получен")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByRoleName(
        @RequestParam("roleName") String roleName) {
        List<EmployeeDto> employees = employeeService.getEmployeesByRoleNameNative(roleName);
        return ResponseEntity.ok(employees);
    }

    @GetMapping
    @Operation(summary = "Получить всех сотрудников",
        description = "Возвращает список всех сотрудников с фильтром по зарплате")
    @ApiResponse(responseCode = "200", description = "Список сотрудников успешно получен")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(
        @RequestParam(value = "min_salary", required = false) BigDecimal minSalary,
        @RequestParam(value = "max_salary", required = false) BigDecimal maxSalary) {
        List<Employee> employees = employeeService
            .getEmployeesBySalaryRange(minSalary, maxSalary);
        List<EmployeeDto> employeeDtos = employees.stream()
            .map(employeeMapper::toDto)
            .toList();
        return ResponseEntity.ok(employeeDtos);
    }

    @PostMapping
    @Operation(summary = "Создать сотрудника",
        description = "Создает нового сотрудника")
    @ApiResponse(responseCode = "201", description = "Сотрудник успешно создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<EmployeeDto> createEmployee(
        @Valid @RequestBody EmployeeCreateDto employeeDto) {
        EmployeeDto createdEmployee = employeeService.createEmployee(employeeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Создать несколько сотрудников",
        description = "Создает несколько сотрудников одновременно")
    @ApiResponse(responseCode = "201", description = "Сотрудники успешно созданы")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<List<EmployeeDto>> createEmployeesBulk(
        @Valid @RequestBody List<EmployeeCreateDto> employeeDtos) {
        List<EmployeeDto> createdEmployees = employeeService.createEmployeesBulk(employeeDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployees);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить сотрудника",
        description = "Обновляет существующего сотрудника по ID")
    @ApiResponse(responseCode = "200", description = "Сотрудник успешно обновлен")
    @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<EmployeeDto> updateEmployee(
        @PathVariable Long id, @Valid @RequestBody EmployeeCreateDto employeeDto) {
        EmployeeDto updatedEmployee = employeeService.updateEmployee(id, employeeDto);
        if (updatedEmployee == null) {
            throw new ResourceNotFoundException("Employee not found with id " + id);
        }
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить сотрудника", description = "Удаляет сотрудника по ID")
    @ApiResponse(responseCode = "204", description = "Сотрудник успешно удален")
    @ApiResponse(responseCode = "404", description = "Сотрудник не найден")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}