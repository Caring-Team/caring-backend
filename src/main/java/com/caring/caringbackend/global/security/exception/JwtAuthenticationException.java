package com.caring.caringbackend.global.security.exception;


import org.springframework.security.authentication.InsufficientAuthenticationException;

public class JwtAuthenticationException extends InsufficientAuthenticationException {
    public JwtAuthenticationException(String message) {
        super(message);
    }
}