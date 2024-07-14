package com.example.module1.service;



import com.example.module1.dto.MedecinResponseDTO;
import com.example.module1.entities.AppUser;
import com.example.module1.entities.ConfirmationToken;
import com.example.module1.entities.Medecin;
import com.example.module1.exception.MedecinException;
import com.example.module1.exception.MedecinNotFoundException;
import com.example.module1.mappers.MedecineMapper;
import com.example.module1.repository.ConfirmationTokenRepository;
import com.example.module1.repository.MedecinRepository;
import com.example.module1.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class MedecinServiceImpl implements MedecinService {


    private static final long EXPIRATION_TIME_MS = 60 * 60 * 1000;

    private MedecinRepository medecinRepository;
    private UserRepository userRepository;
    private MedecineMapper medecineMapper;

    private ConfirmationTokenRepository confirmationTokenRepository;
    private JavaMailSender mailSender;


    public MedecinResponseDTO saveMedecin(Medecin medecin) throws MedecinException {
        if (medecinRepository.existsByCin(medecin.getCin())) {
            throw new MedecinException("Le numéro de CIN spécifié est déjà utilisé par un autre utilisateur");
        }
        if (medecinRepository.existsByInpe(medecin.getInpe())) {
            throw new MedecinException("Le numéro INPE spécifié est déjà utilisé par un autre utilisateur");
        }
        if (medecinRepository.existsByPpr(medecin.getPpr())) {
            throw new MedecinException("Le numéro PPR spécifié est déjà utilisé par un autre utilisateur");
        }
        if (userRepository.existsByMail(medecin.getAppUser().getMail())) {
            throw new MedecinException("L'email spécifié est déjà utilisé par un autre utilisateur");
        }

        Medecin savedMedecin = medecinRepository.save(medecin);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setMedecin(savedMedecin);
        confirmationToken.setCreatedDate(new Date());
        confirmationToken.setToken(token);
        confirmationTokenRepository.save(confirmationToken);

        new Thread(() -> sendConfirmationEmail(savedMedecin.getAppUser().getMail(), token)).start();

        return medecineMapper.fromMedcine(savedMedecin);
    }

    public MedecinResponseDTO getMedecinById(Long id) throws MedecinNotFoundException {
        Optional<Medecin> medecinOptional = medecinRepository.findById(id);
        if (medecinOptional.isEmpty()) {
            throw new MedecinNotFoundException("Médecin non trouvé avec l'ID : " + id);
        }
        MedecinResponseDTO medecinResponseDTO=medecineMapper.fromMedcine(medecinOptional.get());
        return medecinResponseDTO;
    }

    public MedecinResponseDTO updateMedecinPartial(Long id, Map<String, Object> updates) throws MedecinNotFoundException {
        Medecin existingMedecin = medecinRepository.findById(id)
                .orElseThrow(() -> new MedecinNotFoundException("Medecin not found with id " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "nom":
                    existingMedecin.getAppUser().setNom((String) value);
                    break;
                case "prenom":
                    existingMedecin.getAppUser().setPrenom((String) value);
                    break;
                case "mail":
                    existingMedecin.getAppUser().setMail((String) value);
                    break;
                case "numTele":
                    existingMedecin.getAppUser().setNumTele((String) value);
                    break;
                case "password":
                    existingMedecin.getAppUser().setPassword((String) value);
                    break;
                case "cin":
                    existingMedecin.setCin((String) value);
                    break;
                case "inpe":
                    existingMedecin.setInpe((String) value);
                    break;
                case "ppr":
                    existingMedecin.setPpr((String) value);
                    break;
                case "estMedcinESJ":
                    existingMedecin.setEstMedcinESJ((Boolean) value);
                    break;
                case "estGeneraliste":
                    existingMedecin.setEstGeneraliste((Boolean) value);
                    break;
                case "specialite":
                    existingMedecin.setSpecialite((String) value);
                    break;
                case "confirmed":
                    existingMedecin.setConfirmed((Boolean) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid attribute: " + key);
            }
        });

        userRepository.save(existingMedecin.getAppUser());
        medecinRepository.save(existingMedecin);

        return medecineMapper.fromMedcine(existingMedecin);
    }

    @Override
    public void deleteMedecin(Long id) throws MedecinNotFoundException, MedecinException {
        Optional<Medecin> medecinOptional = medecinRepository.findById(id);
        if (medecinOptional.isPresent()) {
            try {
                medecinRepository.delete(medecinOptional.get());
            } catch (Exception e) {
                throw new MedecinException("Une erreur s'est produite lors de la suppression du médecin", e);
            }
        } else {
            throw new MedecinNotFoundException("Médecin non trouvé avec l'ID : " + id);
        }
    }

    @Override
    public List<MedecinResponseDTO> getAllMedecins() {
        List<Medecin> medecins=medecinRepository.findAll();
        return medecins.stream().map(m->medecineMapper.fromMedcine(m)).collect(Collectors.toList());
    }


    @Override
    public Medecin confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        if (confirmationToken != null) {
            Date now = new Date();
            Medecin medecin = confirmationToken.getMedecin();
            long diffMs = now.getTime() - confirmationToken.getCreatedDate().getTime();
            if (diffMs > EXPIRATION_TIME_MS) {
                throw new RuntimeException("Confirmation token has expired");
            }
            medecin.setConfirmed(true);
            medecinRepository.save(medecin);
            return medecin;
        } else {
            throw new RuntimeException("Invalid confirmation token");
        }
    }
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
    public void sendConfirmationEmail(String to, String token) {
        String confirmationUrl = "http://localhost:8080/register/medecins/confirmation?token=" + token;
        String subject = "Email Confirmation";
        String htmlBody = "<p>Please confirm your email by clicking the following link:</p>"
                + "<p><a href=\"" + confirmationUrl + "\">Confirm Email</a></p>";

        sendEmail(to, subject, htmlBody);
    }

}
