package com.example.module1.exception;

public class ProfessionnelSanteNotFoundException  extends  Exception{
    public ProfessionnelSanteNotFoundException(String message) {
        super(message);
    }

    public ProfessionnelSanteNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
