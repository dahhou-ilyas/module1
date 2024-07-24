package com.example.module1.service;


import com.example.module1.dto.MedecinResponseDTO;
import com.example.module1.entities.ConfirmationToken;
import com.example.module1.entities.Medecin;
import com.example.module1.exception.MedecinException;
import com.example.module1.exception.MedecinNotFoundException;
import com.example.module1.exception.UserNotFoundException;
import com.example.module1.mappers.MedecineMapper;
import com.example.module1.repository.ConfirmationTokenRepository;
import com.example.module1.repository.MedecinRepository;
import com.example.module1.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class MedecinServiceImpl implements MedecinService {

    private MedecinRepository medecinRepository;
    private UserRepository userRepository;
    private MedecineMapper medecineMapper;
    private PasswordEncoder passwordEncoder;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private ConfirmeMailService confirmeMailService;

    private JwtEncoder jwtEncoder;


    private AuthenticationManager authenticationManagerMedecin;
    public MedecinServiceImpl(MedecinRepository medecinRepository,
                              UserRepository userRepository,
                              MedecineMapper medecineMapper,
                              PasswordEncoder passwordEncoder,
                              ConfirmationTokenRepository confirmationTokenRepository,
                              ConfirmeMailService confirmeMailService, JwtEncoder jwtEncoder,
                              @Qualifier("authenticationManagerMedecin") AuthenticationManager authenticationManagerMedecin) {
        this.medecinRepository = medecinRepository;
        this.userRepository = userRepository;
        this.medecineMapper = medecineMapper;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.confirmeMailService = confirmeMailService;
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerMedecin = authenticationManagerMedecin;
    }


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
                    existingMedecin.getInfoUser().setFirstAuth((Boolean) value);
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

    public Map<String, String> confirmAuthentification(Long id,String password) throws BadRequestException {
        try {
            // Rechercher le médecin par ID
            Medecin medecin = medecinRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Medecin not found with ID: " + id));

            // Mettre à jour le champ isFirstAuth
            medecin.getInfoUser().setFirstAuth(false);
            medecinRepository.save(medecin);

            // Authentifier le médecin pour générer un nouveau token
            Authentication authentication = authenticationManagerMedecin.authenticate(
                    new UsernamePasswordAuthenticationToken(medecin.getInfoUser().getMail(), password));

            Instant instant = Instant.now();
            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            // Préparer les claims pour le JWT
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", medecin.getInfoUser().getMail());
            claims.put("role", scope);
            claims.put("id", medecin.getId());
            claims.put("nom", medecin.getInfoUser().getNom());
            claims.put("prenom", medecin.getInfoUser().getPrenom());
            claims.put("mail", medecin.getInfoUser().getMail());
            claims.put("confirmed", medecin.getInfoUser().isConfirmed());
            claims.put("isFirstAuth", medecin.getInfoUser().isFirstAuth());

            // Créer le JWT
            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .issuedAt(instant)
                    .expiresAt(instant.plus(30, ChronoUnit.MINUTES))
                    .subject(medecin.getInfoUser().getMail())
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
