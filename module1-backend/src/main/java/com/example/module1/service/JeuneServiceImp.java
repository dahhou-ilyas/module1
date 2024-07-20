package com.example.module1.service;

import com.example.module1.dto.JeuneDto;
import com.example.module1.entities.*;
import com.example.module1.enums.NiveauEtudes;
import com.example.module1.enums.Sexe;
import com.example.module1.enums.Situation;
import com.example.module1.exception.EmailNonValideException;
import com.example.module1.exception.JeuneException;
import com.example.module1.exception.JeuneNotFoundException;
import com.example.module1.exception.PhoneNonValideException;
import com.example.module1.mappers.JeuneMapper;
import com.example.module1.mappers.JeuneNonScolariseMapper;
import com.example.module1.mappers.JeuneScolariseMapper;
import com.example.module1.repository.AntecedentFamilialRepo;
import com.example.module1.repository.AntecedentPersonnelRepo;
import com.example.module1.repository.ConfirmationTokenRepository;
import com.example.module1.repository.JeuneRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Service
@Transactional
@AllArgsConstructor
public class JeuneServiceImp implements JeuneService {

    private static final long EXPIRATION_TIME_MS = 60 * 60 * 1000;

    private final JeuneMapper jeuneMapper;
    private final JeuneNonScolariseMapper jeuneNonScolariseMapper;
    private final JeuneScolariseMapper jeuneScolariseMapper;

    private final Validator validator;
    private final JeuneRepo jeuneRepo;

    private final AntecedentFamilialRepo antecedentFamilialRepo;
    private final AntecedentPersonnelRepo antecedentPersonnelRepo;
    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private ConfirmeMailService confirmeMailService;

    @Override
    public JeuneDto saveJeune(Jeune jeune) throws EmailNonValideException, PhoneNonValideException {
        validateJeuneInfo(jeune);

        jeune.getInfoUser().setPassword(passwordEncoder.encode(jeune.getInfoUser().getPassword()));
        jeune.setAge(calculateAge(jeune.getDateNaissance()));
        jeune.setIdentifiantPatient(generateIdentifiantPatient());

        Jeune savedJeune = jeuneRepo.save(jeune);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setJeune(savedJeune);
        confirmationToken.setCreatedDate(new Date());
        confirmationToken.setToken(token);
        confirmationTokenRepository.save(confirmationToken);

        new Thread(() -> confirmeMailService.sendConfirmationEmail(savedJeune.getInfoUser().getMail(), token)).start();

        return jeuneMapper.toDtoJeune(savedJeune);
    }

    private void validateJeuneInfo(Jeune jeune) throws EmailNonValideException, PhoneNonValideException  {
        if (!validator.isValidEmail(jeune.getInfoUser().getMail())) {
            throw new EmailNonValideException("Invalid email format");
        }
        if (!validator.isValidPhoneNumber(jeune.getInfoUser().getNumTele())) {
            throw new PhoneNonValideException("Invalid phone number format");
        }
//        if (jeune.getAge() >= 16 && !isValidCIN(jeune.getCin())) {
//            throw new CinNonValideException("Invalid CIN format");
//        }
    }

    private int calculateAge(Date birthDate) {
        LocalDate localBirthDate = birthDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return Period.between(localBirthDate, LocalDate.now()).getYears();
    }

    private int generateIdentifiantPatient() {
        return new Random().nextInt(900000) + 100000;
    }



    @Override
    public AntecedentFamilial addAntecedentFamilial(Long jeuneId, AntecedentFamilial antecedentFamilial) {
        jeuneRepo.findById(jeuneId)
                .ifPresentOrElse(jeune -> {
                            antecedentFamilial.setJeune(jeune);
                            antecedentFamilialRepo.save(antecedentFamilial);
                        },
                        () -> {
                            throw new IllegalArgumentException("Jeune not found");
                        });
        return antecedentFamilial;
    }

    @Override
    public AntecedentPersonnel addAntecedentPersonnel(Long jeuneId, AntecedentPersonnel antecedentPersonnel) {
        jeuneRepo.findById(jeuneId)
                .ifPresentOrElse(jeune -> {
                            antecedentPersonnel.setJeune(jeune);
                            antecedentPersonnelRepo.save(antecedentPersonnel);
                        },
                        () -> {
                            throw new IllegalArgumentException("Jeune not found");
                        });
        return antecedentPersonnel;
    }


    @Override
    public Map<String, Object> getAntecedents(Long jeuneId) throws JeuneException {
        Jeune jeune = jeuneRepo.findById(jeuneId)
                .orElseThrow(() -> new JeuneException("Jeune n'existe pas"));

        Map<String, Object> result = new HashMap<>();
        result.put("AntecedentFamilial", antecedentFamilialRepo.findByJeune(jeune).orElse(null));
        result.put("AntecedentPersonnel", antecedentPersonnelRepo.findByJeune(jeune).orElse(null));

        return result;
    }

    @Override
    public Object getJeuneById(Long id) throws JeuneNotFoundException {
        Jeune jeune = jeuneRepo.findById(id)
                .orElseThrow(() -> new JeuneNotFoundException("Jeune not found for this id :: " + id));

        if (jeune instanceof JeuneScolarise) {
            return jeuneScolariseMapper.toDtoJeuneS((JeuneScolarise) jeune);
        } else if (jeune instanceof JeuneNonScolarise) {
            return jeuneNonScolariseMapper.toDtoJeuneNS((JeuneNonScolarise) jeune);
        } else {
            return jeuneMapper.toDtoJeune(jeune);
        }
    }

    public JeuneDto updateJeunePartial(Long id, Map<String, Object> updates) throws JeuneNotFoundException {
        Jeune jeune = jeuneRepo.findById(id)
                .orElseThrow(() -> new JeuneNotFoundException("Jeune not found with id " + id));

        updates.forEach((key, value) -> {
            switch (key) {
                case "nom":
                    jeune.getInfoUser().setNom((String) value);
                    break;
                case "prenom":
                    jeune.getInfoUser().setPrenom((String) value);
                    break;
                case "mail":
                    jeune.getInfoUser().setMail((String) value);
                    break;
                case "numTele":
                    jeune.getInfoUser().setNumTele((String) value);
                    break;
                case "password":
                    jeune.getInfoUser().setPassword((String) value);
                    break;
                case "sexe":
                    jeune.setSexe(Sexe.valueOf((String) value));
                    break;
                case "age":
                    jeune.setAge((Integer) value);
                    break;
                case "identifiantPatient":
                    jeune.setIdentifiantPatient((Integer) value);
                    break;
                case "cin":
                    jeune.setCin((String) value);
                    break;
                case "scolarise":
                    jeune.setScolarise((Boolean) value);
                    break;
                case "confirmed":
                    jeune.getInfoUser().setConfirmed((Boolean) value);
                    break;
                case "isFirstAuth":
                    jeune.getInfoUser().setIsFirstAuth((Boolean) value);
                    break;
                // Mise à jour des propriétés spécifiques selon le type de jeune
                case "dernierNiveauEtudes":
                    if (jeune instanceof JeuneNonScolarise) {
                        ((JeuneNonScolarise) jeune).setDernierNiveauEtudes(NiveauEtudes.valueOf((String) value));
                    }
                    break;
                case "situationActuelle":
                    if (jeune instanceof JeuneNonScolarise) {
                        ((JeuneNonScolarise) jeune).setEnActivite((Boolean) value);
                    }
                    break;
                case "niveauEtudesActuel":
                    if (jeune instanceof JeuneScolarise) {
                        ((JeuneScolarise) jeune).setNiveauEtudesActuel(NiveauEtudes.valueOf((String) value));
                    }
                    break;
                case "cne":
                    if (jeune instanceof JeuneScolarise) {
                        ((JeuneScolarise) jeune).setCNE((String) value);
                    }
                    break;
                case "codeMASSAR":
                    if (jeune instanceof JeuneScolarise) {
                        ((JeuneScolarise) jeune).setCodeMASSAR((String) value);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid attribute: " + key);
            }
        });

        // Enregistrer les changements
        Jeune updatedJeune = jeuneRepo.save(jeune);


        return jeuneMapper.toDtoJeune(updatedJeune);

    }




}
