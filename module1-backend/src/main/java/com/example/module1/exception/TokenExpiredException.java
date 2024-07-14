package com.example.module1.exception;

public class TokenExpiredException extends ConfirmationMailException {
    public TokenExpiredException() {
        super("Confirmation token has expired");
    }
}
