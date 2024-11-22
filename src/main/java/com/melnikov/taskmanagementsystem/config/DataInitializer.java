package com.melnikov.taskmanagementsystem.config;

import com.melnikov.taskmanagementsystem.model.Role;
import com.melnikov.taskmanagementsystem.model.User;
import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import com.melnikov.taskmanagementsystem.repository.RoleRepository;
import com.melnikov.taskmanagementsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (roleRepository.findRoleByName(RoleName.ROLE_USER) == null){
            Role userRole = new Role();
            userRole.setName(RoleName.ROLE_USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findRoleByName(RoleName.ROLE_ADMIN) == null){
            Role adminRole = new Role();
            adminRole.setName(RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }

        if (userRepository.findUserByEmail("admin@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRole(roleRepository.findRoleByName(RoleName.ROLE_ADMIN));
            userRepository.save(admin);
        }

        if (userRepository.findUserByEmail("user@example.com").isEmpty()) {
            User user = new User();
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user"));
            user.setRole(roleRepository.findRoleByName(RoleName.ROLE_USER));
            userRepository.save(user);
        }
    }
}
