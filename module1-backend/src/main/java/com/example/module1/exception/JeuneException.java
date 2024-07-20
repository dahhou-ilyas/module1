package com.example.module1.exception;

public class JeuneException extends Exception {

    public JeuneException() {
        super();
    }

    public JeuneException(String message) {
        super(message);
    }

    public JeuneException(String message, Throwable cause) {
        super(message, cause);
    }

    public JeuneException(Throwable cause) {
        super(cause);
    }
}

