package com.resume.backend.exceptions;

import org.springframework.security.core.AuthenticationException;

public class JwtExpiredAuthenticationException extends AuthenticationException {
    public JwtExpiredAuthenticationException(String msg) {
        super(msg);
    }
}
