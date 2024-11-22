package com.melnikov.taskmanagementsystem.exception.task;

public class AssigneeNotFoundException extends RuntimeException {
    public AssigneeNotFoundException(String message) {
        super(message);
    }
}