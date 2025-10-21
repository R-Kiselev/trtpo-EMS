package com.example.employeemanagementsystem.dao;

import com.example.employeemanagementsystem.model.Employee;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDao extends JpaRepository<Employee, Long> {
    List<Employee> findBySalaryBetween(BigDecimal minSalary, BigDecimal maxSalary);

    List<Employee> findBySalaryGreaterThanEqual(BigDecimal minSalary);

    List<Employee> findBySalaryLessThanEqual(BigDecimal maxSalary);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByPositionId(Long positionId);

    @Query("SELECT e FROM Employee e JOIN e.department d JOIN e.position p "
         + "WHERE d.id = :departmentId AND p.id = :positionId")
    List<Employee> findByDepartmentIdAndPositionId(
        @Param("departmentId") Long departmentId,
        @Param("positionId") Long positionId);

    @Query(value =
          "SELECT e.* FROM employees e "
        + "JOIN users u ON e.user_id = u.id "
        + "JOIN user_roles ur ON u.id = ur.user_id "
        + "JOIN roles r ON ur.role_id = r.id "
        + "WHERE r.name = :roleName", nativeQuery = true)
    List<Employee> findByRoleNameNative(@Param("roleName") String roleName);
}