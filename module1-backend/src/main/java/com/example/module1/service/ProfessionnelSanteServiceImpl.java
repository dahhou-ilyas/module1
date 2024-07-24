package com.example.module1.service;


import com.example.module1.dto.ProfessionnelSanteResponseDTO;
import com.example.module1.entities.ConfirmationToken;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.ProfessionnelSanteException;
import com.example.module1.exception.ProfessionnelSanteNotFoundException;
import com.example.module1.exception.UserNotFoundException;
import com.example.module1.mappers.ProfessionnelSanteMapper;
import com.example.module1.repository.ConfirmationTokenRepository;
import com.example.module1.repository.ProfessionnelSanteRepository;
import com.example.module1.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional

public class ProfessionnelSanteServiceImpl implements ProfessionnelSanteService {


    private final ProfessionnelSanteRepository professionnelSanteRepository;
    private final UserRepository userRepository;
    private final ProfessionnelSanteMapper professionnelSanteMapper;

    private ConfirmationTokenRepository confirmationTokenRepository;
    private PasswordEncoder passwordEncoder;
    private ConfirmeMailService confirmeMailService;

    private JwtEncoder jwtEncoder;

    private final AuthenticationManager authenticationManagerProfessionelSante;

    public ProfessionnelSanteServiceImpl(
            ProfessionnelSanteRepository professionnelRepository,
            UserRepository userRepository, ProfessionnelSanteMapper professionnelMapper,
            ConfirmationTokenRepository confirmationTokenRepository, PasswordEncoder passwordEncoder,
            ConfirmeMailService confirmeMailService,
            JwtEncoder jwtEncoder,
            @Qualifier("authenticationManagerProfessionelSante") AuthenticationManager authenticationManagerProfessionelSante) {
        this.professionnelSanteRepository = professionnelRepository;
        this.userRepository = userRepository;
        this.professionnelSanteMapper = professionnelMapper;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmeMailService = confirmeMailService;
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerProfessionelSante = authenticationManagerProfessionelSante;
    }


    @Override
    public ProfessionnelSanteResponseDTO saveProfessionnelSante(ProfessionnelSante professionnelSante) throws ProfessionnelSanteException {

        try {
            professionnelSante.getInfoUser().setMotDePasse(passwordEncoder.encode(professionnelSante.getInfoUser().getMotDePasse()));
            ProfessionnelSante savedProfessionnelSante = professionnelSanteRepository.save(professionnelSante);

            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken();
            confirmationToken.setProfessionnelSante(savedProfessionnelSante);
            confirmationToken.setCreatedDate(new Date());
            confirmationToken.setToken(token);
            confirmationTokenRepository.save(confirmationToken);

            new Thread(() -> confirmeMailService.sendConfirmationEmail(savedProfessionnelSante.getInfoUser().getMail(), token)).start();

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
                    existingProfessionnelSante.getInfoUser().setConfirmed((Boolean) value);
                    break;
                case "isFirstAuth":
                    existingProfessionnelSante.getInfoUser().setFirstAuth((Boolean) value);
                    break;
                case "nom":
                    existingProfessionnelSante.getInfoUser().setNom((String) value);
                    break;
                case "prenom":
                    existingProfessionnelSante.getInfoUser().setPrenom((String) value);
                    break;
                case "numTele":
                    existingProfessionnelSante.getInfoUser().setNumTel((String) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid attribute: " + key);
            }
        });

        userRepository.save(existingProfessionnelSante.getInfoUser());
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

    public Map<String, String> confirmAuthentification(Long id,String password) throws BadRequestException {
        try {
            // Rechercher le médecin par ID
            ProfessionnelSante professionnelSante = professionnelSanteRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Medecin not found with ID: " + id));

            // Mettre à jour le champ isFirstAuth
            professionnelSante.getInfoUser().setFirstAuth(false);
            professionnelSanteRepository.save(professionnelSante);

            // Authentifier le médecin pour générer un nouveau token
            Authentication authentication = authenticationManagerProfessionelSante.authenticate(
                    new UsernamePasswordAuthenticationToken(professionnelSante.getInfoUser().getMail(), password));

            Instant instant = Instant.now();
            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            // Préparer les claims pour le JWT
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", professionnelSante.getInfoUser().getMail());
            claims.put("role", scope);
            claims.put("id", professionnelSante.getId());
            claims.put("nom", professionnelSante.getInfoUser().getNom());
            claims.put("prenom", professionnelSante.getInfoUser().getPrenom());
            claims.put("mail", professionnelSante.getInfoUser().getMail());
            claims.put("confirmed", professionnelSante.getInfoUser().isConfirmed());
            claims.put("isFirstAuth", professionnelSante.getInfoUser().isFirstAuth());

            // Créer le JWT
            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .issuedAt(instant)
                    .expiresAt(instant.plus(30, ChronoUnit.MINUTES))
                    .subject(professionnelSante.getInfoUser().getMail())
                    .claim("claims", claims)
                    .build();

            JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS512).build(),
                    jwtClaimsSet
            );

            String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();

            // Retourner le nouveau token
            return Map.of("access-token", jwt);
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Invalid username or password");
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
