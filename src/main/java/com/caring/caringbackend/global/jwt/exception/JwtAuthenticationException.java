package com.caring.caringbackend.global.jwt.exception;


import org.springframework.security.authentication.InsufficientAuthenticationException;

public class JwtAuthenticationException extends InsufficientAuthenticationException {
    public JwtAuthenticationException(String message) {
        super(message);
    }
}