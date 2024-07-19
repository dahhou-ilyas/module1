package com.example.module1.exception;

public class ProfessionnelSanteException extends RuntimeException{
    public ProfessionnelSanteException(String message) {
        super(message);
    }

    public ProfessionnelSanteException(String message, Throwable cause) {
        super(message, cause);
    }
}
