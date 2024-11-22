package com.melnikov.taskmanagementsystem.repository;

import com.melnikov.taskmanagementsystem.model.Role;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role userRole;
    private User user;

    @BeforeEach
    public void setUp() {
        userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);
        roleRepository.save(userRole);

        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(userRole);
        userRepository.save(user);
    }

    @Test
    public void testFindUserByEmail() {
        Optional<User> foundUser = userRepository.findUserByEmail("test@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    public void testFindUserByEmailNotFound() {
        Optional<User> foundUser = userRepository.findUserByEmail("nonexistent@example.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testSaveUser() {
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setPassword("password");
        newUser.setRole(userRole);
        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("new@example.com", savedUser.getEmail());
    }

    @Test
    public void testDeleteUser() {
        userRepository.delete(user);
        Optional<User> deletedUser = userRepository.findUserByEmail("test@example.com");
        assertFalse(deletedUser.isPresent());
    }

    @Test
    public void testUniqueEmail() {
        User duplicateUser = new User();
        duplicateUser.setEmail("test@example.com");
        duplicateUser.setPassword("password");
        duplicateUser.setRole(userRole);

        assertThrows(Exception.class, () -> userRepository.save(duplicateUser));
    }
}