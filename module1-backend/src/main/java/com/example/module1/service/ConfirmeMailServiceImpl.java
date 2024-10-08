package com.example.module1.service;

import com.example.module1.entities.ConfirmationToken;
import com.example.module1.entities.Jeune;
import com.example.module1.entities.Medecin;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.*;
import com.example.module1.repository.ConfirmationTokenRepository;
import com.example.module1.repository.JeuneRepo;
import com.example.module1.repository.MedecinRepository;
import com.example.module1.repository.ProfessionnelSanteRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ConfirmeMailServiceImpl implements ConfirmeMailService {
    private static final long EXPIRATION_TIME_MS = 60 * 60 * 1000;
    private JavaMailSender mailSender;

    private MedecinRepository medecinRepository;
    private ProfessionnelSanteRepository professionnelSanteRepository;
    private JeuneRepo jeuneRepo;

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
                medecin.getInfoUser().setConfirmed(true);
                medecinRepository.save(medecin);
                return medecin;
            } else if (confirmationToken.getProfessionnelSante() != null) {
                ProfessionnelSante professionnelSante = confirmationToken.getProfessionnelSante();
                professionnelSante.getInfoUser().setConfirmed(true);
                professionnelSanteRepository.save(professionnelSante);
                return professionnelSante;

            } else if (confirmationToken.getJeune() !=null) {
                Jeune jeune=confirmationToken.getJeune();
                jeune.getInfoUser().setConfirmed(true);
                jeuneRepo.save(jeune);
                return jeune;
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

    @Override
    public void resendToken(String email) throws MedecinNotFoundException,ProfessionnelSanteNotFoundException,UserNotFoundException,JeuneNotFoundException{
        Optional<Medecin> medecinOpt = medecinRepository.findByMail(email);
        Optional<ProfessionnelSante> professionnelSanteOpt = professionnelSanteRepository.findByMail(email);
        Optional<Jeune> jeuneOpt = jeuneRepo.findByMail(email);



        if (!medecinOpt.isPresent() && !professionnelSanteOpt.isPresent() && !jeuneOpt.isPresent()) {
            throw new UserNotFoundException("User not found");
        }

        if (medecinOpt.isPresent()) {
            resendTokenForMedecin(medecinOpt.get());
        } else if (professionnelSanteOpt.isPresent()) {
            resendTokenForProfessionnelSante(professionnelSanteOpt.get());
        } else if (jeuneOpt.isPresent()) {
            resendTokenForJeune(jeuneOpt.get());
        }
    }
    private void resendTokenForProfessionnelSante(ProfessionnelSante professionnelSante) {
        ConfirmationToken existingToken = confirmationTokenRepository.findByProfessionnelSante(professionnelSante);
        if (existingToken != null) {
            confirmationTokenRepository.delete(existingToken);
            confirmationTokenRepository.flush();
        }
        ConfirmationToken newToken = new ConfirmationToken();
        newToken.setProfessionnelSante(professionnelSante);
        newToken.setCreatedDate(new Date());
        newToken.setToken(UUID.randomUUID().toString());
        confirmationTokenRepository.save(newToken);
        sendConfirmationEmail(professionnelSante.getInfoUser().getMail(), newToken.getToken());
    }
    private void resendTokenForMedecin(Medecin medecin) {
        ConfirmationToken existingToken = confirmationTokenRepository.findByMedecin(medecin);
        if (existingToken != null) {
            confirmationTokenRepository.delete(existingToken);
            confirmationTokenRepository.flush();
        }
        ConfirmationToken newToken = new ConfirmationToken();
        newToken.setMedecin(medecin);
        newToken.setCreatedDate(new Date());
        newToken.setToken(UUID.randomUUID().toString());
        confirmationTokenRepository.save(newToken);
        sendConfirmationEmail(medecin.getInfoUser().getMail(), newToken.getToken());
    }

    private void resendTokenForJeune(Jeune jeune) {
        ConfirmationToken existingToken = confirmationTokenRepository.findByJeune(jeune);
        if (existingToken != null) {
            confirmationTokenRepository.delete(existingToken);
            confirmationTokenRepository.flush();
        }
        ConfirmationToken newToken = new ConfirmationToken();
        newToken.setJeune(jeune);
        newToken.setCreatedDate(new Date());
        newToken.setToken(UUID.randomUUID().toString());
        confirmationTokenRepository.save(newToken);
        sendConfirmationEmail(jeune.getInfoUser().getMail(), newToken.getToken());
    }
}
