package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dao.EmployeeDao;
import com.example.employeemanagementsystem.dao.UserDao;
import com.example.employeemanagementsystem.dto.create.EmployeeCreateDto;
import com.example.employeemanagementsystem.dto.get.DepartmentDto;
import com.example.employeemanagementsystem.dto.get.EmployeeDto;
import com.example.employeemanagementsystem.dto.get.PositionDto;
import com.example.employeemanagementsystem.dto.get.UserDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.exception.ValidationException;
import com.example.employeemanagementsystem.mapper.EmployeeMapper;
import com.example.employeemanagementsystem.model.Department;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.model.Position;
import com.example.employeemanagementsystem.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeDao employeeDao;

    @Mock
    private UserDao userDao;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee testEmployee;
    private User testUser;
    private Department testDepartment;
    private Position testPosition;
    private EmployeeCreateDto testEmployeeCreateDto;
    private EmployeeDto testEmployeeDto;
    private UserDto testUserDto;
    private DepartmentDto testDepartmentDto;
    private PositionDto testPositionDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testDepartment = new Department();
        testDepartment.setId(1L);
        testDepartment.setName("Test Department");

        testPosition = new Position();
        testPosition.setId(1L);
        testPosition.setName("Test Position");

        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setEmail("john.doe@example.com");
        testEmployee.setHireDate(LocalDate.of(2023, 1, 1));
        testEmployee.setSalary(BigDecimal.valueOf(50000));
        testEmployee.setIsActive(true);
        testEmployee.setUser(testUser);
        testEmployee.setDepartment(testDepartment);
        testEmployee.setPosition(testPosition);

        testEmployeeCreateDto = new EmployeeCreateDto();
        testEmployeeCreateDto.setFirstName("John");
        testEmployeeCreateDto.setLastName("Doe");
        testEmployeeCreateDto.setEmail("john.doe@example.com");
        testEmployeeCreateDto.setHireDate(LocalDate.of(2023, 1, 1));
        testEmployeeCreateDto.setSalary(BigDecimal.valueOf(50000));
        testEmployeeCreateDto.setIsActive(true);
        testEmployeeCreateDto.setUserId(1L);
        testEmployeeCreateDto.setDepartmentId(1L);
        testEmployeeCreateDto.setPositionId(1L);

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testUser");

        testDepartmentDto = new DepartmentDto();
        testDepartmentDto.setId(1L);
        testDepartmentDto.setName("Test Department");

        testPositionDto = new PositionDto();
        testPositionDto.setId(1L);
        testPositionDto.setName("Test Position");

        testEmployeeDto = new EmployeeDto();
        testEmployeeDto.setId(1L);
        testEmployeeDto.setFirstName("John");
        testEmployeeDto.setLastName("Doe");
        testEmployeeDto.setEmail("john.doe@example.com");
        testEmployeeDto.setHireDate(LocalDate.of(2023, 1, 1));
        testEmployeeDto.setSalary(BigDecimal.valueOf(50000));
        testEmployeeDto.setIsActive(true);
        testEmployeeDto.setUser(testUserDto);
        testEmployeeDto.setDepartment(testDepartmentDto);
        testEmployeeDto.setPosition(testPositionDto);
    }

    @Test
    void createEmployee_WithValidUser_ShouldReturnEmployeeDto() {
        when(employeeMapper.toEntity(testEmployeeCreateDto)).thenReturn(testEmployee);
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(employeeDao.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        EmployeeDto result = employeeService.createEmployee(testEmployeeCreateDto);

        assertNotNull(result, "EmployeeDto should not be null");
        assertEquals(testEmployeeDto.getId(), result.getId(), "Returned DTO ID should match");
        assertEquals(testEmployeeDto.getFirstName(), result.getFirstName(), "First name should match");
        assertEquals(testEmployeeDto.getUser().getId(), result.getUser().getId(), "User ID should match");
        assertEquals(testEmployeeDto.getDepartment().getId(), result.getDepartment().getId(), "Department ID should match");
        assertEquals(testEmployeeDto.getPosition().getId(), result.getPosition().getId(), "Position ID should match");
        verify(userDao, times(1)).findById(1L);
        verify(employeeDao, times(1)).save(testEmployee);
        verify(employeeMapper, times(1)).toEntity(testEmployeeCreateDto);
        verify(employeeMapper, times(1)).toDto(testEmployee);
    }

    @Test
    void createEmployee_WithInvalidUser_ShouldThrowException() {
        when(employeeMapper.toEntity(testEmployeeCreateDto)).thenReturn(testEmployee);
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
            () -> employeeService.createEmployee(testEmployeeCreateDto));

        assertEquals("User not found with id 1", exception.getMessage(), "Exception message should match");
        verify(userDao, times(1)).findById(1L);
        verify(employeeDao, never()).save(any());
    }

    @Test
    void updateEmployee_WithValidData_ShouldReturnUpdatedEmployeeDto() {
        when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeDao.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        EmployeeDto result = employeeService.updateEmployee(1L, testEmployeeCreateDto);

        assertNotNull(result, "Updated EmployeeDto should not be null");
        assertEquals(testEmployeeDto.getId(), result.getId(), "Returned DTO ID should match");
        assertEquals(testEmployeeDto.getFirstName(), result.getFirstName(), "First name should match");
        verify(employeeDao, times(1)).findById(1L);
        verify(employeeDao, times(1)).save(testEmployee);
        verify(employeeMapper, times(1)).toDto(testEmployee);
        verify(userDao, never()).findById(anyLong());
    }

    @Test
    void updateEmployee_WithDifferentUserId_ShouldUpdateUser() {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newUser");

        UserDto newUserDto = new UserDto();
        newUserDto.setId(2L);
        newUserDto.setUsername("newUser");

        testEmployeeCreateDto.setUserId(2L);
        testEmployeeDto.setUser(newUserDto);

        when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(userDao.findById(2L)).thenReturn(Optional.of(newUser));
        when(employeeDao.save(testEmployee)).thenReturn(testEmployee);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        EmployeeDto result = employeeService.updateEmployee(1L, testEmployeeCreateDto);

        assertNotNull(result, "Updated EmployeeDto should not be null");
        assertEquals(testEmployeeDto.getId(), result.getId(), "Returned DTO ID should match");
        assertEquals(testEmployeeDto.getUser().getId(), result.getUser().getId(), "User ID should match new user");
        verify(employeeDao, times(1)).findById(1L);
        verify(userDao, times(1)).findById(2L);
        verify(employeeDao, times(1)).save(testEmployee);
        verify(employeeMapper, times(1)).toDto(testEmployee);
    }

    @Test
    void updateEmployee_WithNonExistentEmployee_ShouldThrowException() {
        when(employeeDao.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
            () -> employeeService.updateEmployee(1L, testEmployeeCreateDto));

        assertEquals("Employee not found with id 1", exception.getMessage(), "Exception message should match");
        verify(employeeDao, times(1)).findById(1L);
        verify(employeeDao, never()).save(any());
    }

    @Test
    void getEmployeeById_WhenEmployeeExists_ShouldReturnEmployee() {
        when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));

        Optional<Employee> result = employeeService.getEmployeeById(1L);

        assertTrue(result.isPresent(), "Employee should be present");
        assertEquals(testEmployee, result.get(), "Returned employee should match test employee");
        verify(employeeDao, times(1)).findById(1L);
    }

    @Test
    void getEmployeeById_WhenEmployeeNotExists_ShouldReturnEmpty() {
        when(employeeDao.findById(1L)).thenReturn(Optional.empty());

        Optional<Employee> result = employeeService.getEmployeeById(1L);

        assertFalse(result.isPresent(), "Employee should not be present");
        verify(employeeDao, times(1)).findById(1L);
    }

    @Test
    void getEmployeeDtoById_WhenEmployeeExists_ShouldReturnEmployeeDto() {
        when(employeeDao.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        EmployeeDto result = employeeService.getEmployeeDtoById(1L);

        assertNotNull(result, "EmployeeDto should not be null");
        assertEquals(testEmployeeDto.getId(), result.getId(), "Returned DTO ID should match");
        verify(employeeDao, times(1)).findById(1L);
        verify(employeeMapper, times(1)).toDto(testEmployee);
    }

    @Test
    void getEmployeeDtoById_WhenEmployeeNotExists_ShouldThrowException() {
        when(employeeDao.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
            () -> employeeService.getEmployeeDtoById(1L));

        assertEquals("Employee not found with id 1", exception.getMessage(), "Exception message should match");
        verify(employeeDao, times(1)).findById(1L);
    }

    @Test
    void getAllEmployees_ShouldReturnListOfEmployeeDtos() {
        List<Employee> employees = Collections.singletonList(testEmployee);
        when(employeeDao.findAll()).thenReturn(employees);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        List<EmployeeDto> result = employeeService.getAllEmployees();

        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(1, result.size(), "Result list should have one employee");
        assertEquals(testEmployeeDto.getId(), result.get(0).getId(), "Returned DTO ID should match");
        verify(employeeDao, times(1)).findAll();
        verify(employeeMapper, times(1)).toDto(testEmployee);
    }

    @Test
    void deleteEmployee_WhenEmployeeExists_ShouldCallDelete() {
        when(employeeDao.existsById(1L)).thenReturn(true);
        doNothing().when(employeeDao).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeDao, times(1)).existsById(1L);
        verify(employeeDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteEmployee_WhenEmployeeNotExists_ShouldThrowException() {
        when(employeeDao.existsById(1L)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class,
            () -> employeeService.deleteEmployee(1L));

        assertEquals("Employee not found with id 1", exception.getMessage(), "Exception message should match");
        verify(employeeDao, times(1)).existsById(1L);
        verify(employeeDao, never()).deleteById(anyLong());
    }

    @Test
    void getEmployeesBySalaryRange_WithMinAndMax_ShouldReturnFilteredList() {
        BigDecimal minSalary = BigDecimal.valueOf(40000);
        BigDecimal maxSalary = BigDecimal.valueOf(60000);
        List<Employee> employees = Collections.singletonList(testEmployee);
        when(employeeDao.findBySalaryBetween(minSalary, maxSalary)).thenReturn(employees);

        List<Employee> result = employeeService.getEmployeesBySalaryRange(minSalary, maxSalary);

        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(testEmployee, result.get(0), "Returned employee should match test employee");
        verify(employeeDao, times(1)).findBySalaryBetween(minSalary, maxSalary);
    }

    @Test
    void getEmployeesBySalaryRange_WithMinOnly_ShouldReturnFilteredList() {
        BigDecimal minSalary = BigDecimal.valueOf(40000);
        List<Employee> employees = Collections.singletonList(testEmployee);
        when(employeeDao.findBySalaryGreaterThanEqual(minSalary)).thenReturn(employees);

        List<Employee> result = employeeService.getEmployeesBySalaryRange(minSalary, null);

        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(testEmployee, result.get(0), "Returned employee should match test employee");
        verify(employeeDao, times(1)).findBySalaryGreaterThanEqual(minSalary);
    }

    @Test
    void getEmployeesBySalaryRange_WithMaxOnly_ShouldReturnFilteredList() {
        BigDecimal maxSalary = BigDecimal.valueOf(60000);
        List<Employee> employees = Collections.singletonList(testEmployee);
        when(employeeDao.findBySalaryLessThanEqual(maxSalary)).thenReturn(employees);

        List<Employee> result = employeeService.getEmployeesBySalaryRange(null, maxSalary);

        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(testEmployee, result.get(0), "Returned employee should match test employee");
        verify(employeeDao, times(1)).findBySalaryLessThanEqual(maxSalary);
    }

    @Test
    void getEmployeesBySalaryRange_WithNullMinAndMax_ShouldReturnAllEmployees() {
        List<Employee> employees = Collections.singletonList(testEmployee);
        when(employeeDao.findAll()).thenReturn(employees);

        List<Employee> result = employeeService.getEmployeesBySalaryRange(null, null);

        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(testEmployee, result.get(0), "Returned employee should match test employee");
        verify(employeeDao, times(1)).findAll();
    }

    @Test
    void getEmployeesByDepartmentId_ShouldReturnEmployeeDtos() {
        List<Employee> employees = Collections.singletonList(testEmployee);
        when(employeeDao.findByDepartmentId(1L)).thenReturn(employees);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        List<EmployeeDto> result = employeeService.getEmployeesByDepartmentId(1L);

        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(1, result.size(), "Result list should have one employee");
        assertEquals(testEmployeeDto.getId(), result.get(0).getId(), "Returned DTO ID should match");
        verify(employeeDao, times(1)).findByDepartmentId(1L);
        verify(employeeMapper, times(1)).toDto(testEmployee);
    }

    @Test
    void getEmployeesByPositionId_ShouldReturnEmployeeDtos() {
        List<Employee> employees = Collections.singletonList(testEmployee);
        when(employeeDao.findByPositionId(1L)).thenReturn(employees);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        List<EmployeeDto> result = employeeService.getEmployeesByPositionId(1L);

        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(1, result.size(), "Result list should have one employee");
        assertEquals(testEmployeeDto.getId(), result.get(0).getId(), "Returned DTO ID should match");
        verify(employeeDao, times(1)).findByPositionId(1L);
        verify(employeeMapper, times(1)).toDto(testEmployee);
    }

    @Test
    void getEmployeesByDepartmentIdAndPositionId_ShouldReturnEmployeeDtos() {
        List<Employee> employees = Collections.singletonList(testEmployee);
        when(employeeDao.findByDepartmentIdAndPositionId(1L, 1L)).thenReturn(employees);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        List<EmployeeDto> result = employeeService.getEmployeesByDepartmentIdAndPositionId(1L, 1L);

        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(1, result.size(), "Result list should have one employee");
        assertEquals(testEmployeeDto.getId(), result.get(0).getId(), "Returned DTO ID should match");
        verify(employeeDao, times(1)).findByDepartmentIdAndPositionId(1L, 1L);
        verify(employeeMapper, times(1)).toDto(testEmployee);
    }

    @Test
    void getEmployeesByRoleNameNative_ShouldReturnEmployeeDtos() {
        List<Employee> employees = Collections.singletonList(testEmployee);
        when(employeeDao.findByRoleNameNative("ROLE_USER")).thenReturn(employees);
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);

        List<EmployeeDto> result = employeeService.getEmployeesByRoleNameNative("ROLE_USER");

        assertFalse(result.isEmpty(), "Result list should not be empty");
        assertEquals(1, result.size(), "Result list should have one employee");
        assertEquals(testEmployeeDto.getId(), result.get(0).getId(), "Returned DTO ID should match");
        verify(employeeDao, times(1)).findByRoleNameNative("ROLE_USER");
        verify(employeeMapper, times(1)).toDto(testEmployee);
    }

    @Test
    void updateEmployeeWithoutDto_WithValidEmployee_ShouldReturnUpdatedEmployee() {
        when(employeeDao.save(testEmployee)).thenReturn(testEmployee);

        Employee result = employeeService.updateEmployeeWithoutDto(testEmployee);

        assertNotNull(result, "Updated Employee should not be null");
        assertEquals(testEmployee.getId(), result.getId(), "Returned Employee ID should match");
        verify(employeeDao, times(1)).save(testEmployee);
    }

    @Test
    void createEmployeesBulk_WithValidDtos_ShouldReturnEmployeeDtos() {
        EmployeeCreateDto secondEmployeeCreateDto = new EmployeeCreateDto();
        secondEmployeeCreateDto.setFirstName("Jane");
        secondEmployeeCreateDto.setLastName("Smith");
        secondEmployeeCreateDto.setEmail("jane.smith@example.com");
        secondEmployeeCreateDto.setHireDate(LocalDate.of(2023, 2, 1));
        secondEmployeeCreateDto.setSalary(BigDecimal.valueOf(60000));
        secondEmployeeCreateDto.setIsActive(true);
        secondEmployeeCreateDto.setUserId(2L);
        secondEmployeeCreateDto.setDepartmentId(1L);
        secondEmployeeCreateDto.setPositionId(1L);

        Employee secondEmployee = new Employee();
        secondEmployee.setId(2L);
        secondEmployee.setFirstName("Jane");
        secondEmployee.setLastName("Smith");

        EmployeeDto secondEmployeeDto = new EmployeeDto();
        secondEmployeeDto.setId(2L);
        secondEmployeeDto.setFirstName("Jane");
        secondEmployeeDto.setLastName("Smith");

        List<EmployeeCreateDto> dtos = Arrays.asList(testEmployeeCreateDto, secondEmployeeCreateDto);

        when(employeeMapper.toEntity(testEmployeeCreateDto)).thenReturn(testEmployee);
        when(employeeMapper.toEntity(secondEmployeeCreateDto)).thenReturn(secondEmployee);
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDao.findById(2L)).thenReturn(Optional.of(new User()));
        when(employeeDao.saveAll(anyList())).thenReturn(Arrays.asList(testEmployee, secondEmployee));
        when(employeeMapper.toDto(testEmployee)).thenReturn(testEmployeeDto);
        when(employeeMapper.toDto(secondEmployee)).thenReturn(secondEmployeeDto);

        List<EmployeeDto> result = employeeService.createEmployeesBulk(dtos);

        assertNotNull(result, "Result list should not be null");
        assertEquals(2, result.size(), "Result list should contain two EmployeeDtos");
        assertEquals(testEmployeeDto.getId(), result.get(0).getId(), "First EmployeeDto ID should match");
        assertEquals(secondEmployeeDto.getId(), result.get(1).getId(), "Second EmployeeDto ID should match");
        verify(userDao, times(1)).findById(1L);
        verify(userDao, times(1)).findById(2L);
        verify(employeeDao, times(1)).saveAll(anyList());
        verify(employeeMapper, times(2)).toEntity(any());
        verify(employeeMapper, times(2)).toDto(any());
    }

    @Test
    void createEmployeesBulk_WithNullList_ShouldThrowValidationException() {
        Exception exception = assertThrows(ValidationException.class,
            () -> employeeService.createEmployeesBulk(null));

        assertEquals("Employee list cannot be null or empty", exception.getMessage(), "Exception message should match");
        verify(employeeMapper, never()).toEntity(any());
        verify(userDao, never()).findById(anyLong());
        verify(employeeDao, never()).saveAll(anyList());
    }

    @Test
    void createEmployeesBulk_WithEmptyList_ShouldThrowValidationException() {
        Exception exception = assertThrows(ValidationException.class,
            () -> employeeService.createEmployeesBulk(Collections.emptyList()));

        assertEquals("Employee list cannot be null or empty", exception.getMessage(), "Exception message should match");
        verify(employeeMapper, never()).toEntity(any());
        verify(userDao, never()).findById(anyLong());
        verify(employeeDao, never()).saveAll(anyList());
    }

    @Test
    void createEmployeesBulk_WithNullDtoInList_ShouldThrowValidationException() {
        List<EmployeeCreateDto> dtos = Arrays.asList(testEmployeeCreateDto, null);

        // Mock userDao for the valid DTO to prevent ResourceNotFoundException
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(employeeMapper.toEntity(testEmployeeCreateDto)).thenReturn(testEmployee);

        Exception exception = assertThrows(ValidationException.class,
            () -> employeeService.createEmployeesBulk(dtos));

        assertEquals("Employee DTO or user ID cannot be null in bulk creation", exception.getMessage(), "Exception message should match");
        verify(userDao, times(1)).findById(1L);
        verify(employeeMapper, times(1)).toEntity(testEmployeeCreateDto);
        verify(employeeDao, never()).saveAll(anyList());
    }

    @Test
    void createEmployeesBulk_WithNullUserIdInDto_ShouldThrowValidationException() {
        EmployeeCreateDto invalidDto = new EmployeeCreateDto();
        invalidDto.setUserId(null); // Null userId
        List<EmployeeCreateDto> dtos = Arrays.asList(testEmployeeCreateDto, invalidDto);

        // Mock userDao for the valid DTO to prevent ResourceNotFoundException
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(employeeMapper.toEntity(testEmployeeCreateDto)).thenReturn(testEmployee);

        Exception exception = assertThrows(ValidationException.class,
            () -> employeeService.createEmployeesBulk(dtos));

        assertEquals("Employee DTO or user ID cannot be null in bulk creation", exception.getMessage(), "Exception message should match");
        verify(userDao, times(1)).findById(1L);
        verify(employeeMapper, times(1)).toEntity(testEmployeeCreateDto);
        verify(employeeDao, never()).saveAll(anyList());
    }

    @Test
    void createEmployeesBulk_WithNonExistentUser_ShouldThrowResourceNotFoundException() {
        EmployeeCreateDto secondEmployeeCreateDto = new EmployeeCreateDto();
        secondEmployeeCreateDto.setFirstName("Jane");
        secondEmployeeCreateDto.setLastName("Smith");
        secondEmployeeCreateDto.setEmail("jane.smith@example.com");
        secondEmployeeCreateDto.setHireDate(LocalDate.of(2023, 2, 1));
        secondEmployeeCreateDto.setSalary(BigDecimal.valueOf(60000));
        secondEmployeeCreateDto.setIsActive(true);
        secondEmployeeCreateDto.setUserId(2L);
        secondEmployeeCreateDto.setDepartmentId(1L);
        secondEmployeeCreateDto.setPositionId(1L);

        List<EmployeeCreateDto> dtos = Arrays.asList(testEmployeeCreateDto, secondEmployeeCreateDto);

        when(employeeMapper.toEntity(any())).thenReturn(testEmployee);
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(userDao.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class,
            () -> employeeService.createEmployeesBulk(dtos));

        assertEquals("User not found with id 2", exception.getMessage(), "Exception message should match");
        verify(userDao, times(1)).findById(1L);
        verify(userDao, times(1)).findById(2L);
        verify(employeeMapper, times(2)).toEntity(any());
        verify(employeeDao, never()).saveAll(anyList());
    }
}