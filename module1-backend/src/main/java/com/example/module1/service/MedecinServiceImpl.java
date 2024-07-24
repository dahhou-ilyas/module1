package com.example.module1.service;


import com.example.module1.dto.MedecinResponseDTO;
import com.example.module1.entities.ConfirmationToken;
import com.example.module1.entities.Medecin;
import com.example.module1.exception.MedecinException;
import com.example.module1.exception.MedecinNotFoundException;
import com.example.module1.mappers.MedecineMapper;
import com.example.module1.repository.ConfirmationTokenRepository;
import com.example.module1.repository.MedecinRepository;
import com.example.module1.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class MedecinServiceImpl implements MedecinService {



    private MedecinRepository medecinRepository;
    private UserRepository userRepository;
    private MedecineMapper medecineMapper;

    private PasswordEncoder passwordEncoder;

    private ConfirmationTokenRepository confirmationTokenRepository;
    private ConfirmeMailService confirmeMailService;


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
        if (userRepository.existsByMail(medecin.getInfoUser().getMail())) {
            throw new MedecinException("L'email spécifié est déjà utilisé par un autre utilisateur");
        }

        medecin.getInfoUser().setMotDePasse(passwordEncoder.encode(medecin.getInfoUser().getMotDePasse()));
        Medecin savedMedecin = medecinRepository.save(medecin);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setMedecin(savedMedecin);
        confirmationToken.setCreatedDate(new Date());
        confirmationToken.setToken(token);
        confirmationTokenRepository.save(confirmationToken);

        new Thread(() -> confirmeMailService.sendConfirmationEmail(savedMedecin.getInfoUser().getMail(), token)).start();

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
                    existingMedecin.getInfoUser().setNom((String) value);
                    break;
                case "prenom":
                    existingMedecin.getInfoUser().setPrenom((String) value);
                    break;
                case "mail":
                    existingMedecin.getInfoUser().setMail((String) value);
                    break;
                case "numTele":
                    existingMedecin.getInfoUser().setNumTel((String) value);
                    break;
                case "password":
                    existingMedecin.getInfoUser().setMotDePasse((String) value);
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
                    existingMedecin.getInfoUser().setConfirmed((Boolean) value);
                    break;
                case "isFirstAuth":
                    existingMedecin.getInfoUser().setIsFirstAuth((Boolean) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid attribute: " + key);
            }
        });

        userRepository.save(existingMedecin.getInfoUser());
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


}
