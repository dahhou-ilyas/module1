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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

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


    @Override
    public MedecinResponseDTO saveMecine(Medecin medecin) throws MedecinException {

        try {
            Medecin savedMedecin = medecinRepository.save(medecin);

            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setMedecin(savedMedecin);
            confirmationToken.setCreatedDate(new Date());
            confirmationToken.setToken(token);
            confirmationTokenRepository.save(confirmationToken);
            sendConfirmationEmail(savedMedecin.getAppUser().getMail(),token);
            MedecinResponseDTO medecinResponseDTO = medecineMapper.fromMedcine(savedMedecin);
            return medecinResponseDTO;
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
                String constraintName = cause.getConstraintName();
                if (constraintName.contains("mail")) {
                    throw new MedecinException("L'email spécifié est déjà utilisé par un autre utilisateur");
                } else if (constraintName.contains("cin")) {
                    throw new MedecinException("Le numéro de CIN spécifié est déjà utilisé par un autre utilisateur");
                }
            }
            throw new MedecinException("Une erreur s'est produite lors de l'enregistrement du médecin", e);
        }
    }

    public MedecinResponseDTO getMedecinById(Long id) throws MedecinNotFoundException {
        Optional<Medecin> medecinOptional = medecinRepository.findById(id);
        if (medecinOptional.isEmpty()) {
            throw new MedecinNotFoundException("Médecin non trouvé avec l'ID : " + id);
        }
        MedecinResponseDTO medecinResponseDTO=medecineMapper.fromMedcine(medecinOptional.get());
        return medecinResponseDTO;
    }

    @Override
    public void updateMedecin(Long id, Medecin updatedMedecin) throws MedecinNotFoundException, MedecinException {
        Optional<Medecin> existingMedecinOptional = medecinRepository.findById(id);
        if (existingMedecinOptional.isPresent()){
            Medecin existingMedecin = existingMedecinOptional.get();
            updateAppUserFields(existingMedecin.getAppUser(), updatedMedecin.getAppUser());
            updateMedecinFields(existingMedecin, updatedMedecin);
        }
    }
    private void updateMedecinFields(Medecin existingMedecin, Medecin updatedMedecin) {
        if (updatedMedecin.getCin() != null) {
            existingMedecin.setCin(updatedMedecin.getCin());
        }
        if (updatedMedecin.getInpe() != null) {
            existingMedecin.setInpe(updatedMedecin.getInpe());
        }
        if (updatedMedecin.getPpr() != null) {
            existingMedecin.setPpr(updatedMedecin.getPpr());
        }
        if (updatedMedecin.getEstMedcinESJ() != null) {
            existingMedecin.setEstMedcinESJ(updatedMedecin.getEstMedcinESJ());
        }
        if (updatedMedecin.getEstGeneraliste() != null) {
            existingMedecin.setEstGeneraliste(updatedMedecin.getEstGeneraliste());
        }
        if (updatedMedecin.getSpecialite() != null) {
            existingMedecin.setSpecialite(updatedMedecin.getSpecialite());
        }
    }
    private void updateAppUserFields(AppUser existingAppUser, AppUser updatedAppUser){
        Field[] fields = AppUser.class.getDeclaredFields();
        for (Field field : fields){
            field.setAccessible(true);
            try {
                Object updatedValue = field.get(updatedAppUser);

                if (updatedValue != null) {
                    field.set(existingAppUser, updatedValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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

}
