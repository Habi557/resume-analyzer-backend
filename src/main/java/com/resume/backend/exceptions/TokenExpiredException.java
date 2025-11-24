package com.resume.backend.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String refreshTokenIsNotValid) {
        super(refreshTokenIsNotValid);
    }
}
