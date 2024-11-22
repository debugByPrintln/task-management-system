package com.melnikov.taskmanagementsystem.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse  {
    private int status;
    private String message;
    private String details;
}
