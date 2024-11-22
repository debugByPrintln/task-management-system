package com.melnikov.taskmanagementsystem.dto.create;

import com.melnikov.taskmanagementsystem.model.utils.RoleName;
import lombok.Data;

@Data
public class CreateUserDTO {
    private String email;
    private String password;
    private RoleName role;
}
