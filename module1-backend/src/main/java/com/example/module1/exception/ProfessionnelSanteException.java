package com.example.module1.exception;

public class ProfessionnelSanteException extends Exception{
    public ProfessionnelSanteException(String message) {
        super(message);
    }

    public ProfessionnelSanteException(String message, Throwable cause) {
        super(message, cause);
    }
}
