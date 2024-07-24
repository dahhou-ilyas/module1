package com.example.module1.service;

import com.example.module1.dto.JeuneDto;
import com.example.module1.entities.*;
import com.example.module1.enums.NiveauEtudes;
import com.example.module1.enums.Sexe;
import com.example.module1.exception.*;
import com.example.module1.mappers.JeuneMapper;
import com.example.module1.mappers.JeuneNonScolariseMapper;
import com.example.module1.mappers.JeuneScolariseMapper;
import com.example.module1.repository.AntecedentFamilialRepo;
import com.example.module1.repository.AntecedentPersonnelRepo;
import com.example.module1.repository.ConfirmationTokenRepository;
import com.example.module1.repository.JeuneRepo;
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
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class JeuneServiceImp implements JeuneService {


    private final JeuneMapper jeuneMapper;
    private final JeuneNonScolariseMapper jeuneNonScolariseMapper;
    private final JeuneScolariseMapper jeuneScolariseMapper;

    private final Validator validator;
    private final JeuneRepo jeuneRepo;

    private final AntecedentFamilialRepo antecedentFamilialRepo;
    private final AntecedentPersonnelRepo antecedentPersonnelRepo;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PasswordEncoder passwordEncoder;

    private final ConfirmeMailService confirmeMailService;

    private final AuthenticationManager authenticationManagerJeune;

    private final JwtEncoder jwtEncoder;

    public JeuneServiceImp(JeuneMapper jeuneMapper,
                            JeuneNonScolariseMapper jeuneNonScolariseMapper,
                            JeuneScolariseMapper jeuneScolariseMapper, Validator validator,
                            JeuneRepo jeuneRepo, AntecedentFamilialRepo antecedentFamilialRepo,
                            AntecedentPersonnelRepo antecedentPersonnelRepo,
                            ConfirmationTokenRepository confirmationTokenRepository,
                            PasswordEncoder passwordEncoder, ConfirmeMailService confirmeMailService,
                            @Qualifier("authenticationManagerJeune") AuthenticationManager authenticationManagerJeune,
                            JwtEncoder jwtEncoder) {
        this.jeuneMapper = jeuneMapper;
        this.jeuneNonScolariseMapper = jeuneNonScolariseMapper;
        this.jeuneScolariseMapper = jeuneScolariseMapper;
        this.validator = validator;
        this.jeuneRepo = jeuneRepo;
        this.antecedentFamilialRepo = antecedentFamilialRepo;
        this.antecedentPersonnelRepo = antecedentPersonnelRepo;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.confirmeMailService = confirmeMailService;
        this.authenticationManagerJeune = authenticationManagerJeune;
        this.jwtEncoder = jwtEncoder;
    }

    @Override
    public JeuneDto saveJeune(Jeune jeune) throws EmailNonValideException, PhoneNonValideException {
        validateJeuneInfo(jeune);

        jeune.getInfoUser().setMotDePasse(passwordEncoder.encode(jeune.getInfoUser().getMotDePasse()));
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
        if (!validator.isValidPhoneNumber(jeune.getInfoUser().getNumTel())) {
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
                    jeune.getInfoUser().setNumTel((String) value);
                    break;
                case "password":
                    jeune.getInfoUser().setMotDePasse((String) value);
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
                    jeune.getInfoUser().setFirstAuth((Boolean) value);
                    break;
                // Mise à jour des propriétés spécifiques selon le type de jeune
                case "dernierNiveauEtudes":
                    if (jeune instanceof JeuneNonScolarise) {
                        ((JeuneNonScolarise) jeune).setDerniereNiveauEtudes(NiveauEtudes.valueOf((String) value));
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
                        ((JeuneScolarise) jeune).setCodeMassare((String) value);
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


    public Map<String, String> confirmAuthentification( Long id,String password) throws BadRequestException {
        try {
            // Rechercher l'utilisateur par ID
            Jeune jeune = jeuneRepo.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("Jeune not found with ID: " + id));

            // Mettre à jour le champ isFirstAuth
            jeune.getInfoUser().setFirstAuth(false);
            jeuneRepo.save(jeune);

            // Authentifier l'utilisateur pour générer un nouveau token
            Authentication authentication = authenticationManagerJeune.authenticate(
                    new UsernamePasswordAuthenticationToken(jeune.getInfoUser().getMail(), password));

            Instant instant = Instant.now();
            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            // Préparer les claims pour le JWT
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", jeune.getInfoUser().getMail());
            claims.put("role", scope);
            claims.put("id", jeune.getId());
            claims.put("nom", jeune.getInfoUser().getNom());
            claims.put("prenom", jeune.getInfoUser().getPrenom());
            claims.put("mail", jeune.getInfoUser().getMail());
            claims.put("confirmed", jeune.getInfoUser().isConfirmed());
            claims.put("isFirstAuth", jeune.getInfoUser().isFirstAuth());

            // Créer le JWT
            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .issuedAt(instant)
                    .expiresAt(instant.plus(30, ChronoUnit.MINUTES))
                    .subject(jeune.getInfoUser().getMail())
                    .claim("claims", claims)
                    .build();

            JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS512).build(),
                    jwtClaimsSet
            );

            String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();

            // Retourner le nouveau token
            return Map.of("access-token", jwt);
        } catch (BadCredentialsException | UserNotFoundException ex) {
            throw new BadRequestException("Unable to process request");
        }
    }




}
