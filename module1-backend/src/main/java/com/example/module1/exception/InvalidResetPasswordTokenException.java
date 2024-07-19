package com.example.module1.exception;

public class InvalidResetPasswordTokenException extends RuntimeException{
    public InvalidResetPasswordTokenException(String message){
        super(message);
    }

    public InvalidResetPasswordTokenException(String message, Throwable throwable){
        super(message,throwable);
    }
}
