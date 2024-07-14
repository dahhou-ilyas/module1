package backend.authModule.service;

import backend.authModule.dto.ProfessionnelSanteResponseDTO;
import backend.authModule.entities.AppUser;
import backend.authModule.entities.ConfirmationToken;
import backend.authModule.entities.ProfessionnelSante;
import backend.authModule.exception.ProfessionnelSanteException;
import backend.authModule.exception.ProfessionnelSanteNotFoundException;
import backend.authModule.mappers.ProfessionnelSanteMapper;
import backend.authModule.repository.ConfirmationTokenRepository;
import backend.authModule.repository.ProfessionnelSanteRepository;
import backend.authModule.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ProfessionnelSanteServiceImpl implements ProfessionnelSanteService {

    private static final long EXPIRATION_TIME_MS = 60 * 60 * 1000;

    private ProfessionnelSanteRepository professionnelSanteRepository;
    private UserRepository userRepository;

    private ProfessionnelSanteMapper professionnelSanteMapper;

    private ConfirmationTokenRepository confirmationTokenRepository;
    private JavaMailSender mailSender;

    private PasswordEncoder passwordEncoder;

    @Override
    public ProfessionnelSanteResponseDTO saveProfessionnelSante(ProfessionnelSante professionnelSante) throws ProfessionnelSanteException {

        try {
            AppUser appUser = professionnelSante.getUser();
            appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
            ProfessionnelSante savedProfessionnelSante = professionnelSanteRepository.save(professionnelSante);

            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setProfessionnelSante(savedProfessionnelSante);
            confirmationToken.setCreatedDate(new Date());
            confirmationToken.setToken(token);
            confirmationTokenRepository.save(confirmationToken);

            new Thread(() -> sendConfirmationEmail(savedProfessionnelSante.getUser().getMail(), token)).start();

            ProfessionnelSanteResponseDTO professionnelSanteResponseDTO = professionnelSanteMapper.fromProfessionnelSante(savedProfessionnelSante);
            return professionnelSanteResponseDTO;
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException cause = (ConstraintViolationException) e.getCause();
                String constraintName = cause.getConstraintName();
                if (constraintName.contains("mail")) {
                    throw new ProfessionnelSanteException("L'email spécifié est déjà utilisé par un autre utilisateur");
                } else if (constraintName.contains("cin")) {
                    throw new ProfessionnelSanteException("Le numéro de CIN spécifié est déjà utilisé par un autre utilisateur");
                }
            }
            throw new ProfessionnelSanteException("Une erreur s'est produite lors de l'enregistrement du professionnel de santé", e);
        }
    }

    @Override
    public ProfessionnelSanteResponseDTO getProfessionnelSanteById(Long id) throws ProfessionnelSanteNotFoundException {
        Optional<ProfessionnelSante> professionnelSanteOptional = professionnelSanteRepository.findById(id);
        if (professionnelSanteOptional.isEmpty()) {
            throw new ProfessionnelSanteNotFoundException("Professionnel de santé non trouvé avec l'ID : " + id);
        }
        ProfessionnelSanteResponseDTO professionnelSanteResponseDTO = professionnelSanteMapper.fromProfessionnelSante(professionnelSanteOptional.get());
        return professionnelSanteResponseDTO;
    }

    @Override
    public ProfessionnelSanteResponseDTO updateProfessionnelSantePartial(Long id, Map<String, Object> updates) throws ProfessionnelSanteNotFoundException {
        ProfessionnelSante existingProfessionnelSante = professionnelSanteRepository.findById(id)
                .orElseThrow(() -> new ProfessionnelSanteNotFoundException("Professionnel de santé non trouvé avec l'ID : " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "cin":
                    existingProfessionnelSante.setCin((String) value);
                    break;
                case "inpe":
                    existingProfessionnelSante.setInpe((String) value);
                    break;
                case "confirmed":
                    existingProfessionnelSante.setConfirmed((Boolean) value);
                    break;
                case "isFirstAuth":
                    existingProfessionnelSante.setIsFirstAuth((Boolean) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid attribute: " + key);
            }
        });

        userRepository.save(existingProfessionnelSante.getUser());
        professionnelSanteRepository.save(existingProfessionnelSante);

        return professionnelSanteMapper.fromProfessionnelSante(existingProfessionnelSante);
    }

    @Override
    public List<ProfessionnelSanteResponseDTO> getAllProfessionnelsSante() {
        List<ProfessionnelSante> professionnelsSante = professionnelSanteRepository.findAll();
        return professionnelsSante.stream().map(ps -> professionnelSanteMapper.fromProfessionnelSante(ps)).collect(Collectors.toList());
    }

    @Override
    public void deleteProfessionnelSante(Long id) throws ProfessionnelSanteNotFoundException {
        Optional<ProfessionnelSante> professionnelSanteOptional = professionnelSanteRepository.findById(id);
        if (professionnelSanteOptional.isPresent()) {
            try {
                professionnelSanteRepository.delete(professionnelSanteOptional.get());
            } catch (Exception e) {
                throw new ProfessionnelSanteNotFoundException("Une erreur s'est produite lors de la suppression du professionnel de santé", e);
            }
        } else {
            throw new ProfessionnelSanteNotFoundException("Professionnel de santé non trouvé avec l'ID : " + id);
        }
    }

    @Override
    public ProfessionnelSante confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        if (confirmationToken != null) {
            Date now = new Date();
            ProfessionnelSante professionnelSante = confirmationToken.getProfessionnelSante();
            long diffMs = now.getTime() - confirmationToken.getCreatedDate().getTime();
            if (diffMs > EXPIRATION_TIME_MS) {
                throw new RuntimeException("Confirmation token has expired");
            }
            professionnelSante.setConfirmed(true);
            professionnelSanteRepository.save(professionnelSante);
            return professionnelSante;
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

    public void sendConfirmationEmail(String to, String token) {
        String confirmationUrl = "http://localhost:8080/register/professionnelsante/confirmation?token=" + token;
        String subject = "Email Confirmation";
        String htmlBody = "<p>Please confirm your email by clicking the following link:</p>"
                + "<p><a href=\"" + confirmationUrl + "\">Confirm Email</a></p>";

        sendEmail(to, subject, htmlBody);
    }
}
