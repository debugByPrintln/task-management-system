package com.melnikov.taskmanagementsystem.service;

import com.melnikov.taskmanagementsystem.dto.UserDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateUserDTO;
import com.melnikov.taskmanagementsystem.exception.user.RoleNotFoundException;
import com.melnikov.taskmanagementsystem.exception.user.UserNotFoundException;
import com.melnikov.taskmanagementsystem.model.Role;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import com.melnikov.taskmanagementsystem.repository.RoleRepository;
import com.melnikov.taskmanagementsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;

    private CreateUserDTO createUserDTO;
    private Role role;

    @BeforeEach
    public void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setRole(role);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail("user@example.com");
        userDTO.setPassword("password");
        userDTO.setRole(RoleName.ROLE_USER);

        createUserDTO = new CreateUserDTO();
        createUserDTO.setEmail("user@example.com");
        createUserDTO.setPassword("password");
        createUserDTO.setRole(RoleName.ROLE_USER);
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = Arrays.asList(user);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), users.size());
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);

        Page<UserDTO> result = userService.getAllUsers(PageRequest.of(0, 10));
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        assertEquals("user@example.com", result.getContent().get(0).getEmail());
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        UserDTO foundUser = userService.getUserById(1L);
        assertNotNull(foundUser);
        assertEquals("user@example.com", foundUser.getEmail());
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    public void testCreateUser() {
        when(roleRepository.findRoleByName(RoleName.ROLE_USER)).thenReturn(role);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDTO createdUser = userService.createUser(createUserDTO);
        assertNotNull(createdUser);
        assertEquals("user@example.com", createdUser.getEmail());
    }

    @Test
    public void testCreateUserWithNonExistingRole() {
        when(roleRepository.findRoleByName(RoleName.ROLE_USER)).thenReturn(null);
        assertThrows(RoleNotFoundException.class, () -> userService.createUser(createUserDTO));
    }

    @Test
    public void testUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findRoleByName(RoleName.ROLE_USER)).thenReturn(role);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDTO updatedUser = userService.updateUser(1L, userDTO);
        assertNotNull(updatedUser);
        assertEquals("user@example.com", updatedUser.getEmail());
    }

    @Test
    public void testUpdateUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    public void testUpdateUserWithNonExistingRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findRoleByName(RoleName.ROLE_USER)).thenReturn(null);
        assertThrows(RoleNotFoundException.class, () -> userService.updateUser(1L, userDTO));
    }

    @Test
    public void testDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    public void testGetUserByEmail() {
        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        UserDTO foundUser = userService.getUserByEmail("user@example.com");
        assertNotNull(foundUser);
        assertEquals("user@example.com", foundUser.getEmail());
    }

    @Test
    public void testGetUserByEmailNotFound() {
        when(userRepository.findUserByEmail("user@example.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("user@example.com"));
    }
}