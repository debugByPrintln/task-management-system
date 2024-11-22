package com.melnikov.taskmanagementsystem.repository;

import com.melnikov.taskmanagementsystem.model.Role;
import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private Role userRole;
    private Role adminRole;

    @BeforeEach
    public void setUp() {
        userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);
        roleRepository.save(userRole);

        adminRole = new Role();
        adminRole.setName(RoleName.ROLE_ADMIN);
        roleRepository.save(adminRole);
    }

    @Test
    public void testFindRoleByName() {
        Role foundRole = roleRepository.findRoleByName(RoleName.ROLE_USER);
        assertNotNull(foundRole);
        assertEquals(RoleName.ROLE_USER, foundRole.getName());
    }

    @Test
    public void testFindRoleByNameNotFound() {
        Role foundRole = roleRepository.findRoleByName(RoleName.ROLE_USER);
        assertNotNull(foundRole);
        assertEquals(RoleName.ROLE_USER, foundRole.getName());
    }

    @Test
    public void testUniqueRoleName() {
        Role duplicateRole = new Role();
        duplicateRole.setName(RoleName.ROLE_USER);

        assertThrows(Exception.class, () -> roleRepository.save(duplicateRole));
    }
}