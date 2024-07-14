package com.example.module1.exception;


public class InvalidTokenException extends ConfirmationMailException {
    public InvalidTokenException() {
        super("Invalid confirmation token");
    }
}