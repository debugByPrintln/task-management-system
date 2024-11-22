package com.melnikov.taskmanagementsystem.repository;

import com.melnikov.taskmanagementsystem.model.Role;
import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
     Role findRoleByName(RoleName name);
}
