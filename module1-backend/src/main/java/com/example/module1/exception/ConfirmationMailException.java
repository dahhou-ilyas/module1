package com.example.module1.exception;

public class ConfirmationMailException extends Exception{
    public ConfirmationMailException(String message){
        super(message);
    }

    public ConfirmationMailException(String message, Throwable throwable){
        super(message,throwable);
    }
}
