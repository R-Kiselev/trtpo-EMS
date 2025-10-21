package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Важный импорт

import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(readOnly = true) // Эта аннотация решает LazyInitializationException
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.example.employeemanagementsystem.model.User user = userDao.findByUsername(username)
                .or(() -> userDao.findByEmployeeEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with: " + username));

        // Доступ к user.getRoles() теперь происходит внутри активной транзакции
        return new User(user.getUsername(), user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList()));
    }
}