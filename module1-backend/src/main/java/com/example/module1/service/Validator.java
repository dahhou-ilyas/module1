package com.example.module1.service;

public interface Validator {
    boolean isValidEmail(String email);
    boolean isValidPhoneNumber(String phoneNumber);
    boolean isValidCIN(String cin);
}
