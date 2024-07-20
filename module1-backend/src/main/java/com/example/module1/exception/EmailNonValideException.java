package com.example.module1.exception;

public class EmailNonValideException extends Exception {

    public EmailNonValideException() {super();}

    public EmailNonValideException(String message) {
        super(message);
    }

    public EmailNonValideException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNonValideException(Throwable cause) {
        super(cause);
    }
}
