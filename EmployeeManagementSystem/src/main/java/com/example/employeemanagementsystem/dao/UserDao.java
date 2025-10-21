package com.example.employeemanagementsystem.dao;

import com.example.employeemanagementsystem.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN u.employee e WHERE e.email = :email")
    Optional<User> findByEmployeeEmail(@Param("email") String email);
}