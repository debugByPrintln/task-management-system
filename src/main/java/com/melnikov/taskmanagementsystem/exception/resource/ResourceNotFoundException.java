package com.melnikov.taskmanagementsystem.exception.resource;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
