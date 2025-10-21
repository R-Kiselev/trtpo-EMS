package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dao.DepartmentDao;
import com.example.employeemanagementsystem.dto.create.DepartmentCreateDto;
import com.example.employeemanagementsystem.dto.get.DepartmentDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.exception.ValidationException;
import com.example.employeemanagementsystem.mapper.DepartmentMapper;
import com.example.employeemanagementsystem.model.Department;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.model.User;
import com.example.employeemanagementsystem.utils.InMemoryCache;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentDao departmentDao;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private UserService userService;

    @Mock
    private InMemoryCache<Long, Department> departmentCache;

    @InjectMocks
    private DepartmentService departmentService;

    private Department testDepartment;
    private DepartmentCreateDto testDepartmentCreateDto;
    private DepartmentDto testDepartmentDto;
    private Employee testEmployee;
    private User testUser;

    @BeforeEach
    void setUp() {
        testDepartment = new Department();
        testDepartment.setId(1L);
        testDepartment.setName("Test Department");
        testDepartment.setDescription("Test Description");

        testDepartmentCreateDto = new DepartmentCreateDto();
        testDepartmentCreateDto.setName("Test Department");
        testDepartmentCreateDto.setDescription("Test Description");

        testDepartmentDto = new DepartmentDto();
        testDepartmentDto.setId(1L);
        testDepartmentDto.setName("Test Department");
        testDepartmentDto.setDescription("Test Description");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setUser(testUser);
        testDepartment.setEmployees(Collections.singletonList(testEmployee));
    }

    @Test
    void getDepartmentById_FromCache_ShouldReturnDepartmentDto() {
        when(departmentCache.get(1L)).thenReturn(testDepartment);
        when(departmentMapper.toDto(testDepartment)).thenReturn(testDepartmentDto);

        DepartmentDto result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals(testDepartmentDto.getId(), result.getId());
        assertEquals(testDepartmentDto.getName(), result.getName());
        verify(departmentCache, times(1)).get(1L);
        verify(departmentDao, never()).findById(anyLong());
        verify(departmentMapper, times(1)).toDto(testDepartment);
    }

    @Test
    void getDepartmentById_NotInCache_ShouldReturnDepartmentDto() {
        when(departmentCache.get(1L)).thenReturn(null);
        when(departmentDao.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(departmentMapper.toDto(testDepartment)).thenReturn(testDepartmentDto);

        DepartmentDto result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals(testDepartmentDto.getId(), result.getId());
        assertEquals(testDepartmentDto.getName(), result.getName());
        verify(departmentCache, times(1)).get(1L);
        verify(departmentDao, times(1)).findById(1L);
        verify(departmentCache, times(1)).put(1L, testDepartment);
        verify(departmentMapper, times(1)).toDto(testDepartment);
    }

    @Test
    void getDepartmentById_NonExistent_ShouldThrowResourceNotFoundException() {
        when(departmentCache.get(1L)).thenReturn(null);
        when(departmentDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> departmentService.getDepartmentById(1L));

        assertEquals("Department not found with id 1", exception.getMessage());
        verify(departmentCache, times(1)).get(1L);
        verify(departmentDao, times(1)).findById(1L);
        verify(departmentCache, never()).put(anyLong(), any());
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void getAllDepartments_ShouldReturnListOfDepartmentDtos() {
        List<Department> departments = Collections.singletonList(testDepartment);
        when(departmentDao.findAll()).thenReturn(departments);
        when(departmentMapper.toDto(testDepartment)).thenReturn(testDepartmentDto);

        List<DepartmentDto> result = departmentService.getAllDepartments();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testDepartmentDto.getId(), result.get(0).getId());
        verify(departmentDao, times(1)).findAll();
        verify(departmentMapper, times(1)).toDto(testDepartment);
    }

    @Test
    void getAllDepartments_EmptyList_ShouldReturnEmptyList() {
        when(departmentDao.findAll()).thenReturn(Collections.emptyList());

        List<DepartmentDto> result = departmentService.getAllDepartments();

        assertTrue(result.isEmpty());
        verify(departmentDao, times(1)).findAll();
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void createDepartment_ValidDto_ShouldReturnDepartmentDto() {
        when(departmentDao.findByName("Test Department")).thenReturn(Optional.empty());
        when(departmentMapper.toEntity(testDepartmentCreateDto)).thenReturn(testDepartment);
        when(departmentDao.save(testDepartment)).thenReturn(testDepartment);
        when(departmentMapper.toDto(testDepartment)).thenReturn(testDepartmentDto);

        DepartmentDto result = departmentService.createDepartment(testDepartmentCreateDto);

        assertNotNull(result);
        assertEquals(testDepartmentDto.getId(), result.getId());
        assertEquals(testDepartmentDto.getName(), result.getName());
        verify(departmentDao, times(1)).findByName("Test Department");
        verify(departmentMapper, times(1)).toEntity(testDepartmentCreateDto);
        verify(departmentDao, times(1)).save(testDepartment);
        verify(departmentCache, times(1)).put(1L, testDepartment);
        verify(departmentMapper, times(1)).toDto(testDepartment);
    }

    @Test
    void createDepartment_DuplicateName_ShouldThrowValidationException() {
        when(departmentDao.findByName("Test Department")).thenReturn(Optional.of(testDepartment));

        ValidationException exception = assertThrows(ValidationException.class,
            () -> departmentService.createDepartment(testDepartmentCreateDto));

        assertEquals("Department name already exists", exception.getMessage());
        verify(departmentDao, times(1)).findByName("Test Department");
        verify(departmentMapper, never()).toEntity(any());
        verify(departmentDao, never()).save(any());
        verify(departmentCache, never()).put(anyLong(), any());
    }

    @Test
    void updateDepartment_ValidDto_ShouldReturnUpdatedDepartmentDto() {
        when(departmentDao.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(departmentDao.findByName("Test Department")).thenReturn(Optional.empty());
        doNothing().when(departmentMapper).updateDepartmentFromDto(testDepartmentCreateDto, testDepartment);
        when(departmentDao.save(testDepartment)).thenReturn(testDepartment);
        when(departmentMapper.toDto(testDepartment)).thenReturn(testDepartmentDto);

        DepartmentDto result = departmentService.updateDepartment(1L, testDepartmentCreateDto);

        assertNotNull(result);
        assertEquals(testDepartmentDto.getId(), result.getId());
        assertEquals(testDepartmentDto.getName(), result.getName());
        verify(departmentDao, times(1)).findById(1L);
        verify(departmentDao, times(1)).findByName("Test Department");
        verify(departmentMapper, times(1)).updateDepartmentFromDto(testDepartmentCreateDto, testDepartment);
        verify(departmentDao, times(1)).save(testDepartment);
        verify(departmentCache, times(1)).put(1L, testDepartment);
        verify(departmentMapper, times(1)).toDto(testDepartment);
    }

    @Test
    void updateDepartment_DuplicateName_ShouldThrowValidationException() {
        Department otherDepartment = new Department();
        otherDepartment.setId(2L);
        otherDepartment.setName("Test Department");

        when(departmentDao.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(departmentDao.findByName("Test Department")).thenReturn(Optional.of(otherDepartment));

        ValidationException exception = assertThrows(ValidationException.class,
            () -> departmentService.updateDepartment(1L, testDepartmentCreateDto));

        assertEquals("Department name already exists", exception.getMessage());
        verify(departmentDao, times(1)).findById(1L);
        verify(departmentDao, times(1)).findByName("Test Department");
        verify(departmentMapper, never()).updateDepartmentFromDto(any(), any());
        verify(departmentDao, never()).save(any());
        verify(departmentCache, never()).put(anyLong(), any());
    }

    @Test
    void updateDepartment_SameNameAsCurrent_ShouldAllowUpdate() {
        when(departmentDao.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(departmentDao.findByName("Test Department")).thenReturn(Optional.of(testDepartment));
        doNothing().when(departmentMapper).updateDepartmentFromDto(testDepartmentCreateDto, testDepartment);
        when(departmentDao.save(testDepartment)).thenReturn(testDepartment);
        when(departmentMapper.toDto(testDepartment)).thenReturn(testDepartmentDto);

        DepartmentDto result = departmentService.updateDepartment(1L, testDepartmentCreateDto);

        assertNotNull(result);
        assertEquals(testDepartmentDto.getId(), result.getId());
        verify(departmentDao, times(1)).findById(1L);
        verify(departmentDao, times(1)).findByName("Test Department");
        verify(departmentMapper, times(1)).updateDepartmentFromDto(testDepartmentCreateDto, testDepartment);
        verify(departmentDao, times(1)).save(testDepartment);
        verify(departmentCache, times(1)).put(1L, testDepartment);
        verify(departmentMapper, times(1)).toDto(testDepartment);
    }

    @Test
    void updateDepartment_NonExistent_ShouldThrowResourceNotFoundException() {
        when(departmentDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> departmentService.updateDepartment(1L, testDepartmentCreateDto));

        assertEquals("Department not found with id 1", exception.getMessage());
        verify(departmentDao, times(1)).findById(1L);
        verify(departmentDao, never()).findByName(any());
        verify(departmentMapper, never()).updateDepartmentFromDto(any(), any());
        verify(departmentDao, never()).save(any());
        verify(departmentCache, never()).put(anyLong(), any());
    }

    @Test
    void deleteDepartment_WithEmployees_ShouldDeleteDepartmentAndUsers() {
        when(departmentDao.findById(1L)).thenReturn(Optional.of(testDepartment));
        doNothing().when(userService).deleteUser(1L);
        doNothing().when(departmentDao).delete(testDepartment);

        departmentService.deleteDepartment(1L);

        verify(departmentDao, times(1)).findById(1L);
        verify(userService, times(1)).deleteUser(1L);
        verify(departmentCache, times(1)).evict(1L);
        verify(departmentDao, times(1)).delete(testDepartment);
    }

    @Test
    void deleteDepartment_WithoutEmployees_ShouldDeleteDepartment() {
        testDepartment.setEmployees(Collections.emptyList());
        when(departmentDao.findById(1L)).thenReturn(Optional.of(testDepartment));
        doNothing().when(departmentDao).delete(testDepartment);

        departmentService.deleteDepartment(1L);

        verify(departmentDao, times(1)).findById(1L);
        verify(userService, never()).deleteUser(anyLong());
        verify(departmentCache, times(1)).evict(1L);
        verify(departmentDao, times(1)).delete(testDepartment);
    }

    @Test
    void deleteDepartment_NonExistent_ShouldThrowResourceNotFoundException() {
        when(departmentDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> departmentService.deleteDepartment(1L));

        assertEquals("Department not found with id 1", exception.getMessage());
        verify(departmentDao, times(1)).findById(1L);
        verify(userService, never()).deleteUser(anyLong());
        verify(departmentCache, never()).evict(anyLong());
        verify(departmentDao, never()).delete(any());
    }

    @Test
    void deleteDepartment_WithEmployeeNoUser_ShouldDeleteDepartment() {
        testEmployee.setUser(null);
        testDepartment.setEmployees(Collections.singletonList(testEmployee));
        when(departmentDao.findById(1L)).thenReturn(Optional.of(testDepartment));
        doNothing().when(departmentDao).delete(testDepartment);

        departmentService.deleteDepartment(1L);

        verify(departmentDao, times(1)).findById(1L);
        verify(userService, never()).deleteUser(anyLong());
        verify(departmentCache, times(1)).evict(1L);
        verify(departmentDao, times(1)).delete(testDepartment);
    }
}