package com.example.employeemanagementsystem.config;

import com.example.employeemanagementsystem.dao.RoleDao;
import com.example.employeemanagementsystem.dao.UserDao;
import com.example.employeemanagementsystem.model.Role;
import com.example.employeemanagementsystem.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {

    // CommandLineRunner теперь принимает все три зависимости
    @Bean
    public CommandLineRunner initData(RoleDao roleDao, UserDao userDao, PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Создаем роли, если их нет
            Role userRole = roleDao.findByName("USER").orElseGet(() -> {
                Role role = new Role();
                role.setName("USER");
                return roleDao.save(role);
            });

            Role adminRole = roleDao.findByName("ADMIN").orElseGet(() -> {
                Role role = new Role();
                role.setName("ADMIN");
                return roleDao.save(role);
            });

            // 2. Создаем пользователя-админа, если его нет
            if (userDao.findByUsername("admin").isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                // Обязательно шифруем пароль!
                adminUser.setPassword(passwordEncoder.encode("admin"));
                adminUser.setRoles(Set.of(adminRole, userRole)); // Даем ему обе роли
                userDao.save(adminUser);
            }
        };
    }
}