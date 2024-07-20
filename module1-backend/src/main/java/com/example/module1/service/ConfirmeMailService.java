package com.example.module1.service;

import com.example.module1.exception.*;

public interface ConfirmeMailService {
    void sendEmail(String to, String subject, String body);
    Object confirmEmail(String token) throws ConfirmationMailException;

     void sendConfirmationEmail(String to, String token);

    void resendToken(String email) throws MedecinNotFoundException,ProfessionnelSanteNotFoundException,UserNotFoundException,JeuneNotFoundException;
}
