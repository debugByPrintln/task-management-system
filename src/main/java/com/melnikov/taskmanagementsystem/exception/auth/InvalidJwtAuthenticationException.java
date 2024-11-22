package com.melnikov.taskmanagementsystem.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class InvalidJwtAuthenticationException extends AuthenticationException {

    public InvalidJwtAuthenticationException(String msg) {
        super(msg);
    }
}