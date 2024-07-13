package com.example.module1.service;

import com.example.module1.exception.InvalidResetPasswordTokenException;
import com.example.module1.exception.UserNotFoundException;

public interface PasswordResetService {
    void initiatePasswordReset(String email) throws UserNotFoundException;

    boolean validatePasswordResetToken(String token);

    void resetPassword(String token, String newPassword) throws InvalidResetPasswordTokenException;

}
