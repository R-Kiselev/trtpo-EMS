package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dao.DepartmentDao;
import com.example.employeemanagementsystem.dto.create.DepartmentCreateDto;
import com.example.employeemanagementsystem.dto.get.DepartmentDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.exception.ValidationException;
import com.example.employeemanagementsystem.mapper.DepartmentMapper;
import com.example.employeemanagementsystem.model.Department;
import com.example.employeemanagementsystem.model.Employee;
import com.example.employeemanagementsystem.utils.InMemoryCache;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {

    private static final String DEPARTMENT_NOT_FOUND_MESSAGE = "Department not found with id ";
    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    private final DepartmentDao departmentDao;
    private final DepartmentMapper departmentMapper;
    private final UserService userService;
    private final InMemoryCache<Long, Department> departmentCache;

    @Autowired
    public DepartmentService(
        DepartmentDao departmentDao,
        DepartmentMapper departmentMapper,
        UserService userService,
        InMemoryCache<Long, Department> departmentCache) {
        this.departmentDao = departmentDao;
        this.departmentMapper = departmentMapper;
        this.userService = userService;
        this.departmentCache = departmentCache;
    }

    @Transactional(readOnly = true)
    public DepartmentDto getDepartmentById(Long id) {
        logger.debug("Attempting to retrieve department with id {} from cache.", id);
        Department cachedDepartment = departmentCache.get(id);
        if (cachedDepartment != null) {
            logger.info("Department with id {} retrieved from cache.", id);
            return departmentMapper.toDto(cachedDepartment);
        }

        logger.debug("Department with id {} not found in cache. Retrieving from database.", id);
        Department department = departmentDao
            .findById(id)
            .orElseThrow(() -> {
                logger.warn("Department with id {} not found.", id);
                return new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id);
            });

        logger.info("Department with id {} retrieved from database.", id);
        departmentCache.put(id, department);
        return departmentMapper.toDto(department);
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> getAllDepartments() {
        logger.debug("Retrieving all departments.");
        List<DepartmentDto> departments =  departmentDao.findAll().stream()
            .map(departmentMapper::toDto)
            .collect(Collectors.toList());
        logger.info("Retrieved all departments. Total count: {}", departments.size());
        return departments;
    }

    @Transactional
    public DepartmentDto createDepartment(DepartmentCreateDto departmentDto) {
        logger.debug("Creating new department: {}", departmentDto);
        String name = departmentDto.getName();
        if (departmentDao.findByName(name).isPresent()) {
            logger.warn("Attempt to create department with existing name: {}", name);
            throw new ValidationException("Department name already exists");
        }
        Department department = departmentMapper.toEntity(departmentDto);
        Department savedDepartment = departmentDao.save(department);
        departmentCache.put(savedDepartment.getId(), savedDepartment);
        logger.info("Department with id {} created and added to cache.", savedDepartment.getId());
        return departmentMapper.toDto(savedDepartment);
    }

    @Transactional
    public DepartmentDto updateDepartment(Long id, DepartmentCreateDto departmentDto) {
        logger.debug("Updating department with id {}: {}", id, departmentDto);
        Department department = departmentDao
            .findById(id)
            .orElseThrow(() -> {
                logger.warn("Attempt to update non-existing department with id {}.", id);
                return new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id);
            });

        String newName = departmentDto.getName();
        Department existingDepartmentWithName = departmentDao.findByName(newName).orElse(null);
        if (existingDepartmentWithName != null && !existingDepartmentWithName.getId().equals(id)) {
            logger.warn("Attempt to update department with id {} to an existing name: {}",
                id, newName);
            throw new ValidationException("Department name already exists");
        }

        departmentMapper.updateDepartmentFromDto(departmentDto, department);
        Department updatedDepartment = departmentDao.save(department);
        departmentCache.put(id, updatedDepartment);
        logger.info("Department with id {} updated and cache updated.", id);
        return departmentMapper.toDto(updatedDepartment);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        logger.debug("Deleting department with id {}.", id);
        Department department = departmentDao.findById(id)
            .orElseThrow(() -> {
                logger.warn("Attempt to delete non-existing department with id {}.", id);
                return new ResourceNotFoundException(DEPARTMENT_NOT_FOUND_MESSAGE + id);
            });

        for (Employee employee : department.getEmployees()) {
            if (employee.getUser() != null) {
                userService.deleteUser(employee.getUser().getId());
            }
        }

        departmentCache.evict(id);
        logger.info("Department with id {} removed from cache.", id);
        departmentDao.delete(department);
        logger.info("Department with id {} deleted.", id);
    }
}