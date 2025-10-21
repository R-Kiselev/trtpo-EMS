package com.example.employeemanagementsystem.service;

import com.example.employeemanagementsystem.dao.RoleDao;
import com.example.employeemanagementsystem.dao.UserDao;
import com.example.employeemanagementsystem.dto.create.UserCreateDto;
import com.example.employeemanagementsystem.dto.get.UserDto;
import com.example.employeemanagementsystem.exception.ResourceNotFoundException;
import com.example.employeemanagementsystem.mapper.UserMapper;
import com.example.employeemanagementsystem.model.Role;
import com.example.employeemanagementsystem.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleDao roleDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;
    private Role testRole;

    @BeforeEach
    void setUp() {
        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("ROLE_USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(new HashSet<>(Collections.singletonList(testRole)));

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testUser");
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDto() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getUsername(), result.getUsername());
        verify(userDao, times(1)).findById(1L);
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowResourceNotFoundException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> userService.getUserById(1L));

        assertEquals("User not found with id 1", exception.getMessage());
        verify(userDao, times(1)).findById(1L);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void getUserByUsername_WhenUserExists_ShouldReturnUserDto() {
        when(userDao.findByUsername("testUser")).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUserByUsername("testUser");

        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getUsername(), result.getUsername());
        verify(userDao, times(1)).findByUsername("testUser");
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    void getUserByUsername_WhenUserNotExists_ShouldThrowResourceNotFoundException() {
        when(userDao.findByUsername("testUser")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> userService.getUserByUsername("testUser"));

        assertEquals("User not found with id testUser", exception.getMessage());
        verify(userDao, times(1)).findByUsername("testUser");
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        List<User> users = Collections.singletonList(testUser);
        when(userDao.findAll()).thenReturn(users);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        List<UserDto> result = userService.getAllUsers();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testUserDto.getId(), result.get(0).getId());
        verify(userDao, times(1)).findAll();
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    void getAllUsers_EmptyList_ShouldReturnEmptyList() {
        when(userDao.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
        verify(userDao, times(1)).findAll();
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void createUser_WithRoleIds_ShouldReturnUserDto() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword("rawPassword");
        userCreateDto.setRoleIds(Collections.singleton(1L));

        when(userMapper.toEntity(userCreateDto)).thenReturn(testUser);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(roleDao.findById(1L)).thenReturn(Optional.of(testRole));
        when(userDao.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.createUser(userCreateDto);

        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getUsername(), result.getUsername());
        verify(userMapper, times(1)).toEntity(userCreateDto);
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(roleDao, times(1)).findById(1L);
        verify(userDao, times(1)).save(testUser);
        verify(userMapper, times(1)).toDto(testUser);
        verify(roleService, never()).findRoleByName(anyString());
    }

    @Test
    void createUser_WithoutRoleIds_ShouldAssignDefaultRole() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword("rawPassword");
        userCreateDto.setRoleIds(null);

        when(userMapper.toEntity(userCreateDto)).thenReturn(testUser);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(roleService.findRoleByName("USER")).thenReturn(testRole);
        when(userDao.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.createUser(userCreateDto);

        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        verify(userMapper, times(1)).toEntity(userCreateDto);
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(roleService, times(1)).findRoleByName("USER");
        verify(userDao, times(1)).save(testUser);
        verify(userMapper, times(1)).toDto(testUser);
        verify(roleDao, never()).findById(anyLong());
    }

    @Test
    void createUser_WithInvalidRoleId_ShouldThrowResourceNotFoundException() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword("rawPassword");
        userCreateDto.setRoleIds(Collections.singleton(1L));

        when(userMapper.toEntity(userCreateDto)).thenReturn(testUser);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(roleDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> userService.createUser(userCreateDto));

        assertEquals("Role not found with id 1", exception.getMessage());
        verify(userMapper, times(1)).toEntity(userCreateDto);
        verify(passwordEncoder, times(1)).encode("rawPassword");
        verify(roleDao, times(1)).findById(1L);
        verify(userDao, never()).save(any());
    }

    @Test
    void updateUser_WithPasswordAndRoles_ShouldReturnUpdatedUserDto() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword("newPassword");
        userCreateDto.setRoleIds(Collections.singleton(1L));

        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userMapper).updateUserFromDto(userCreateDto, testUser);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(roleDao.findById(1L)).thenReturn(Optional.of(testRole));
        when(userDao.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(1L, userCreateDto);

        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        verify(userDao, times(1)).findById(1L);
        verify(userMapper, times(1)).updateUserFromDto(userCreateDto, testUser);
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(roleDao, times(1)).findById(1L);
        verify(userDao, times(1)).save(testUser);
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    void updateUser_WithoutPassword_ShouldNotEncodePassword() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword(null);
        userCreateDto.setRoleIds(Collections.singleton(1L));

        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userMapper).updateUserFromDto(userCreateDto, testUser);
        when(roleDao.findById(1L)).thenReturn(Optional.of(testRole));
        when(userDao.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(1L, userCreateDto);

        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        verify(userDao, times(1)).findById(1L);
        verify(userMapper, times(1)).updateUserFromDto(userCreateDto, testUser);
        verify(passwordEncoder, never()).encode(anyString());
        verify(roleDao, times(1)).findById(1L);
        verify(userDao, times(1)).save(testUser);
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    void updateUser_WithoutRoles_ShouldNotUpdateRoles() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword(null);
        userCreateDto.setRoleIds(null);

        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userMapper).updateUserFromDto(userCreateDto, testUser);
        when(userDao.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(1L, userCreateDto);

        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        verify(userDao, times(1)).findById(1L);
        verify(userMapper, times(1)).updateUserFromDto(userCreateDto, testUser);
        verify(passwordEncoder, never()).encode(anyString());
        verify(roleDao, never()).findById(anyLong());
        verify(userDao, times(1)).save(testUser);
        verify(userMapper, times(1)).toDto(testUser);
    }

    @Test
    void updateUser_NonExistent_ShouldThrowResourceNotFoundException() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword("rawPassword");
        userCreateDto.setRoleIds(Collections.singleton(1L));

        when(userDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> userService.updateUser(1L, userCreateDto));

        assertEquals("User not found with id 1", exception.getMessage());
        verify(userDao, times(1)).findById(1L);
        verify(userMapper, never()).updateUserFromDto(any(), any());
        verify(userDao, never()).save(any());
    }

    @Test
    void updateUser_WithInvalidRoleId_ShouldThrowResourceNotFoundException() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setUsername("testUser");
        userCreateDto.setPassword("rawPassword");
        userCreateDto.setRoleIds(Collections.singleton(1L));

        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userMapper).updateUserFromDto(userCreateDto, testUser);
        when(roleDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> userService.updateUser(1L, userCreateDto));

        assertEquals("Role not found with id 1", exception.getMessage());
        verify(userDao, times(1)).findById(1L);
        verify(userMapper, times(1)).updateUserFromDto(userCreateDto, testUser);
        verify(roleDao, times(1)).findById(1L);
        verify(userDao, never()).save(any());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        when(userDao.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userDao).deleteById(1L);

        userService.deleteUser(1L);

        verify(userDao, times(1)).findById(1L);
        verify(userDao, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowResourceNotFoundException() {
        when(userDao.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> userService.deleteUser(1L));

        assertEquals("User not found with id 1", exception.getMessage());
        verify(userDao, times(1)).findById(1L);
        verify(userDao, never()).deleteById(anyLong());
    }
}