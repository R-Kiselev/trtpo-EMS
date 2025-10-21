package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.service.RoleService;
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
@RequestMapping("/api/roles")
@Tag(name = "Role Controller", description = "API для управления ролями")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить роль по ID", description = "Возвращает роль по указанному ID")
    @ApiResponse(responseCode = "200", description = "Роль найдена")
    @ApiResponse(responseCode = "404", description = "Роль не найдена")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        RoleDto roleDto = roleService.getRoleById(id);
        if (roleDto == null) {
            throw new ResourceNotFoundException("Role not found with id " + id);
        }
        return ResponseEntity.ok(roleDto);
    }

    @GetMapping
    @Operation(summary = "Получить все роли", description = "Возвращает список всех ролей")
    @ApiResponse(responseCode = "200", description = "Список ролей успешно получен")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    @Operation(summary = "Создать роль", description = "Создает новую роль")
    @ApiResponse(responseCode = "201", description = "Роль успешно создана")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleCreateDto roleCreateDto) {
        RoleDto createdRole = roleService.createRole(roleCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить роль", description = "Обновляет существующую роль по ID")
    @ApiResponse(responseCode = "200", description = "Роль успешно обновлена")
    @ApiResponse(responseCode = "404", description = "Роль не найдена")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<RoleDto> updateRole(@PathVariable Long id,
                                              @Valid @RequestBody RoleCreateDto roleCreateDto) {
        RoleDto updatedRole = roleService.updateRole(id, roleCreateDto);
        if (updatedRole == null) {
            throw new ResourceNotFoundException("Role not found with id " + id);
        }
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить роль", description = "Удаляет роль по ID")
    @ApiResponse(responseCode = "204", description = "Роль успешно удалена")
    @ApiResponse(responseCode = "404", description = "Роль не найдена")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}