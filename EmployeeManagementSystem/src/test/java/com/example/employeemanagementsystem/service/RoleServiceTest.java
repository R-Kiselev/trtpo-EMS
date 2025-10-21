package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dao.RoleDao;
import com.example.employeemanagementsystem.dto.create.RoleCreateDto;
import com.example.employeemanagementsystem.dto.get.RoleDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.RoleMapper;
import com.example.employeemanagementsystem.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleDao roleDao;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private Role testRole;
    private RoleCreateDto testRoleCreateDto;
    private RoleDto testRoleDto;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("ROLE_USER");

        testRoleCreateDto = new RoleCreateDto();
        testRoleCreateDto.setName("ROLE_USER");

        testRoleDto = new RoleDto();
        testRoleDto.setId(1L);
        testRoleDto.setName("ROLE_USER");
    }

    @Test
    void getRoleById_WhenRoleExists_ShouldReturnRoleDto() {
        when(roleDao.findById(1L)).thenReturn(Optional.of(testRole));
        when(roleMapper.toDto(testRole)).thenReturn(testRoleDto);

        RoleDto result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals(testRoleDto.getId(), result.getId());
        assertEquals(testRoleDto.getName(), result.getName());
        verify(roleDao, times(1)).findById(1L);
        verify(roleMapper, times(1)).toDto(testRole);
    }

    @Test
    void getRoleById_WhenRoleNotExists_ShouldThrowResourceNotFoundException() {
        when(roleDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> roleService.getRoleById(1L));

        assertEquals("Role not found with id 1", exception.getMessage());
        verify(roleDao, times(1)).findById(1L);
        verify(roleMapper, never()).toDto(any());
    }

    @Test
    void getAllRoles_ShouldReturnListOfRoleDtos() {
        List<Role> roles = Collections.singletonList(testRole);
        when(roleDao.findAll()).thenReturn(roles);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleDto);

        List<RoleDto> result = roleService.getAllRoles();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testRoleDto.getId(), result.get(0).getId());
        verify(roleDao, times(1)).findAll();
        verify(roleMapper, times(1)).toDto(testRole);
    }

    @Test
    void getAllRoles_EmptyList_ShouldReturnEmptyList() {
        when(roleDao.findAll()).thenReturn(Collections.emptyList());

        List<RoleDto> result = roleService.getAllRoles();

        assertTrue(result.isEmpty());
        verify(roleDao, times(1)).findAll();
        verify(roleMapper, never()).toDto(any());
    }

    @Test
    void createRole_ValidDto_ShouldReturnRoleDto() {
        when(roleMapper.toEntity(testRoleCreateDto)).thenReturn(testRole);
        when(roleDao.save(testRole)).thenReturn(testRole);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleDto);

        RoleDto result = roleService.createRole(testRoleCreateDto);

        assertNotNull(result);
        assertEquals(testRoleDto.getId(), result.getId());
        assertEquals(testRoleDto.getName(), result.getName());
        verify(roleMapper, times(1)).toEntity(testRoleCreateDto);
        verify(roleDao, times(1)).save(testRole);
        verify(roleMapper, times(1)).toDto(testRole);
    }

    @Test
    void updateRole_ValidDto_ShouldReturnUpdatedRoleDto() {
        when(roleDao.findById(1L)).thenReturn(Optional.of(testRole));
        doNothing().when(roleMapper).updateRoleFromDto(testRoleCreateDto, testRole);
        when(roleDao.save(testRole)).thenReturn(testRole);
        when(roleMapper.toDto(testRole)).thenReturn(testRoleDto);

        RoleDto result = roleService.updateRole(1L, testRoleCreateDto);

        assertNotNull(result);
        assertEquals(testRoleDto.getId(), result.getId());
        assertEquals(testRoleDto.getName(), result.getName());
        verify(roleDao, times(1)).findById(1L);
        verify(roleMapper, times(1)).updateRoleFromDto(testRoleCreateDto, testRole);
        verify(roleDao, times(1)).save(testRole);
        verify(roleMapper, times(1)).toDto(testRole);
    }

    @Test
    void updateRole_NonExistent_ShouldThrowResourceNotFoundException() {
        when(roleDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> roleService.updateRole(1L, testRoleCreateDto));

        assertEquals("Role not found with id 1", exception.getMessage());
        verify(roleDao, times(1)).findById(1L);
        verify(roleMapper, never()).updateRoleFromDto(any(), any());
        verify(roleDao, never()).save(any());
    }

    @Test
    void deleteRole_WhenRoleExists_ShouldDeleteRole() {
        when(roleDao.findById(1L)).thenReturn(Optional.of(testRole));
        doNothing().when(roleDao).deleteById(1L);

        roleService.deleteRole(1L);

        verify(roleDao, times(1)).findById(1L);
        verify(roleDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteRole_WhenRoleNotExists_ShouldThrowResourceNotFoundException() {
        when(roleDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> roleService.deleteRole(1L));

        assertEquals("Role not found with id 1", exception.getMessage());
        verify(roleDao, times(1)).findById(1L);
        verify(roleDao, never()).deleteById(anyLong());
    }

    @Test
    void findRoleByName_WhenRoleExists_ShouldReturnRole() {
        when(roleDao.findByName("ROLE_USER")).thenReturn(Optional.of(testRole));

        Role result = roleService.findRoleByName("ROLE_USER");

        assertNotNull(result);
        assertEquals(testRole.getId(), result.getId());
        assertEquals(testRole.getName(), result.getName());
        verify(roleDao, times(1)).findByName("ROLE_USER");
    }

    @Test
    void findRoleByName_WhenRoleNotExists_ShouldThrowResourceNotFoundException() {
        when(roleDao.findByName("ROLE_USER")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> roleService.findRoleByName("ROLE_USER"));

        assertEquals("Role not found with name ROLE_USER", exception.getMessage());
        verify(roleDao, times(1)).findByName("ROLE_USER");
    }
}