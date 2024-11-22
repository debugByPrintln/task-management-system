package com.melnikov.taskmanagementsystem.service;

import com.melnikov.taskmanagementsystem.dto.UserDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateUserDTO;
import com.melnikov.taskmanagementsystem.exception.user.RoleNotFoundException;
import com.melnikov.taskmanagementsystem.exception.user.UserNotFoundException;
import com.melnikov.taskmanagementsystem.model.Role;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.repository.RoleRepository;
import com.melnikov.taskmanagementsystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pageable: {}", pageable);
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }

    public UserDTO getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
        return convertToDTO(user);
    }

    public UserDTO createUser(CreateUserDTO userDTO) {
        log.info("Creating new user with details: {}", userDTO);
        User user = convertCreateToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user with id: {} and details: {}", id, userDTO);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Role role = roleRepository.findRoleByName(userDTO.getRole());
        if (role == null) {
            log.warn("Role not found with name: {}", userDTO.getRole());
            throw new RoleNotFoundException("Role not found with name: " + userDTO.getRole());
        }
        existingUser.setRole(role);
        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User not found with id: {}", id);
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public UserDTO getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().getName());
        return userDTO;
    }

    private User convertCreateToEntity(CreateUserDTO userDTO){
        User user = new User();
        user.setEmail(userDTO.getEmail());
        Role role = roleRepository.findRoleByName(userDTO.getRole());
        if (role == null) {
            log.warn("Role not found with name: {}", userDTO.getRole());
            throw new RoleNotFoundException("Role not found with name: " + userDTO.getRole());
        }
        user.setRole(role);
        return user;
    }
}