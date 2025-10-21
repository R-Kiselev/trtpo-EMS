package com.example.employeemanagementsystem.dao;

import com.example.employeemanagementsystem.model.Department;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentDao extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);
}