package com.example.module1.service;

public interface ConfirmeMailService<T> {
    void sendEmail(String to, String subject, String body);
    T confirmEmail(String token);

     void sendConfirmationEmail(String to, String token);
}
