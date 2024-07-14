package com.example.module1.service;

import com.example.module1.entities.ConfirmationToken;
import com.example.module1.entities.Medecin;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.ConfirmationMailException;
import com.example.module1.exception.InvalidTokenException;
import com.example.module1.exception.TokenExpiredException;
import com.example.module1.repository.ConfirmationTokenRepository;
import com.example.module1.repository.MedecinRepository;
import com.example.module1.repository.ProfessionnelSanteRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class ConfirmeMailServiceImpl implements ConfirmeMailService {
    private static final long EXPIRATION_TIME_MS = 60 * 60 * 1000;
    private JavaMailSender mailSender;

    private MedecinRepository medecinRepository;
    private ProfessionnelSanteRepository professionnelSanteRepository;

    private ConfirmationTokenRepository confirmationTokenRepository;
    @Override
    public void sendEmail(String to, String subject, String htmlBody) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true indique que le contenu est HTML

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object confirmEmail(String token) throws ConfirmationMailException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        if (confirmationToken != null) {
            Date now = new Date();
            long diffMs = now.getTime() - confirmationToken.getCreatedDate().getTime();
            if (diffMs > EXPIRATION_TIME_MS) {
                throw new TokenExpiredException();
            }

            if (confirmationToken.getMedecin() != null) {
                Medecin medecin = confirmationToken.getMedecin();
                medecin.setConfirmed(true);
                medecinRepository.save(medecin);
                return medecin;
            } else if (confirmationToken.getProfessionnelSante() != null) {
                ProfessionnelSante professionnelSante = confirmationToken.getProfessionnelSante();
                professionnelSante.setConfirmed(true);
                professionnelSanteRepository.save(professionnelSante);
                return professionnelSante;
            } else {
                throw new InvalidTokenException();
            }
        } else {
            throw new InvalidTokenException();
        }
    }

    @Override
    public void sendConfirmationEmail(String to, String token) {
        String confirmationUrl = "http://localhost:8080/register/confirmation?token=" + token;
        String subject = "Email Confirmation";
        String htmlBody = "<p>Please confirm your email by clicking the following link:</p>"
                + "<p><a href=\"" + confirmationUrl + "\">Confirm Email</a></p>";

        sendEmail(to, subject, htmlBody);
    }
}
