package com.example.module1.exception;

public class ProfessionnelSanteNotFoundException  extends  RuntimeException{
    public ProfessionnelSanteNotFoundException(String message) {
        super(message);
    }

    public ProfessionnelSanteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
