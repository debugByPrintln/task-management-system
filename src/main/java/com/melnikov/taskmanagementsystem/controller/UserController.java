package com.melnikov.taskmanagementsystem.controller;

import com.melnikov.taskmanagementsystem.dto.UserDTO;
import com.melnikov.taskmanagementsystem.dto.create.CreateUserDTO;
import com.melnikov.taskmanagementsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a paginated list of all users. FOR ADMIN ONLY.")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pageable: {}", pageable);
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "Retrieve a user by given id. FOR ADMIN ONLY.")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("Fetching user by id: {}", id);
        UserDTO user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            log.warn("User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Create new user with provided email, password and role. FOR ADMIN ONLY.")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO createUserDTO) {
        log.info("Creating new user with details: {}", createUserDTO);
        UserDTO createdUser = userService.createUser(createUserDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user with given id information with provided email, password and role. FOR ADMIN ONLY.")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        log.info("Updating user with id: {} and details: {}", id, userDTO);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            log.warn("User not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete user with given id. FOR ADMIN ONLY.")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve user with provided email address. FOR ADMIN ONLY.")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user by email: {}", email);
        UserDTO user = userService.getUserByEmail(email);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            log.warn("User not found with email: {}", email);
            return ResponseEntity.notFound().build();
        }
    }
}
