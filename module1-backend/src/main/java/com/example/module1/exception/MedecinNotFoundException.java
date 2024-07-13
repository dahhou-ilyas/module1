package com.example.module1.exception;

public class MedecinNotFoundException extends Exception{
    public MedecinNotFoundException(String message) {
        super(message);
    }

    public MedecinNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
