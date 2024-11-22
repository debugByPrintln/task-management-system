package com.melnikov.taskmanagementsystem.dto;

import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String password;
    private RoleName role;
}