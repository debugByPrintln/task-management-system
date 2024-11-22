package com.melnikov.taskmanagementsystem.exception.task;

public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}

