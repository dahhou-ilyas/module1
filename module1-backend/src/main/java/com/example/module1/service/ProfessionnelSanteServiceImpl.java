package com.example.module1.service;


import com.example.module1.dto.ProfessionnelSanteResponseDTO;
import com.example.module1.entities.ConfirmationToken;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.ProfessionnelSanteException;
import com.example.module1.exception.ProfessionnelSanteNotFoundException;
import com.example.module1.mappers.ProfessionnelSanteMapper;
import com.example.module1.repository.ConfirmationTokenRepository;
import com.example.module1.repository.ProfessionnelSanteRepository;
import com.example.module1.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ProfessionnelSanteServiceImpl implements ProfessionnelSanteService {


    private ProfessionnelSanteRepository professionnelSanteRepository;
    private UserRepository userRepository;

    private ProfessionnelSanteMapper professionnelSanteMapper;

    private ConfirmationTokenRepository confirmationTokenRepository;
    private ConfirmeMailService confirmeMailService;


    @Override
    public ProfessionnelSanteResponseDTO saveProfessionnelSante(ProfessionnelSante professionnelSante) throws ProfessionnelSanteException {

        try {
            ProfessionnelSante savedProfessionnelSante = professionnelSanteRepository.save(professionnelSante);

            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setProfessionnelSante(savedProfessionnelSante);
            confirmationToken.setCreatedDate(new Date());
            confirmationToken.setToken(token);
            confirmationTokenRepository.save(confirmationToken);

            new Thread(() -> confirmeMailService.sendConfirmationEmail(savedProfessionnelSante.getUser().getMail(), token)).start();

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
                    existingProfessionnelSante.getUser().setConfirmed((Boolean) value);
                    break;
                case "isFirstAuth":
                    existingProfessionnelSante.getUser().setIsFirstAuth((Boolean) value);
                    break;
                case "nom":
                    existingProfessionnelSante.getUser().setNom((String) value);
                    break;
                case "prenom":
                    existingProfessionnelSante.getUser().setPrenom((String) value);
                    break;
                case "numTele":
                    existingProfessionnelSante.getUser().setNumTele((String) value);
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


}
