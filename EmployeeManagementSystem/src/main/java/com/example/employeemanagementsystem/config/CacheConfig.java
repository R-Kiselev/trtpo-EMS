package com.example.employeemanagementsystem.config;

import com.example.employeemanagementsystem.model.Department;
import com.example.employeemanagementsystem.utils.InMemoryCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public InMemoryCache<Long, Department> departmentCache() {
        return new InMemoryCache<>(128);
    }
}