package com.example.module1.securityConfig;

import com.example.module1.entities.Jeune;
import com.example.module1.entities.Medecin;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.BadRequestException;
import com.example.module1.exception.UserNotFoundException;
import com.example.module1.repository.JeuneRepo;
import com.example.module1.repository.MedecinRepository;
import com.example.module1.repository.ProfessionnelSanteRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/login")
public class SecurityController {
    private final JwtEncoder jwtEncoder;
    private final MedecinRepository medecinRepository;
    private final ProfessionnelSanteRepository professionnelSanteRepository;
    private final JeuneRepo jeuneRepo;

    private final AuthenticationManager authenticationManagerMedecin;

    private final AuthenticationManager authenticationManagerProfessionelSante;
    private final AuthenticationManager authenticationManagerJeune;

    public SecurityController(JwtEncoder jwtEncoder,JeuneRepo jeuneRepo,MedecinRepository medecinRepository,ProfessionnelSanteRepository professionnelSanteRepository,
                              @Qualifier("authenticationManagerMedecin") AuthenticationManager authenticationManagerMedecin,
                              @Qualifier("authenticationManagerProfessionelSante") AuthenticationManager authenticationManagerProfessionelSante,
                              @Qualifier("authenticationManagerJeune") AuthenticationManager authenticationManagerJeune) {
        this.jwtEncoder = jwtEncoder;
        this.jeuneRepo=jeuneRepo;
        this.medecinRepository = medecinRepository;
        this.authenticationManagerMedecin = authenticationManagerMedecin;
        this.authenticationManagerProfessionelSante = authenticationManagerProfessionelSante;
        this.professionnelSanteRepository=professionnelSanteRepository;
        this.authenticationManagerJeune=authenticationManagerJeune;
    }

    @PostMapping("/medecins")
    public ResponseEntity<Map<String, String>> loginMedcin(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            Authentication authentication = authenticationManagerMedecin.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            Instant instant = Instant.now();

            Medecin medecin = medecinRepository.findByCinOrMail(username)
                    .orElseThrow(() -> new UserNotFoundException("Médecin not found with username: " + username));

            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("role", scope);

            claims.put("id", medecin.getId());
            claims.put("nom", medecin.getInfoUser().getNom());
            claims.put("prenom", medecin.getInfoUser().getPrenom());
            claims.put("mail", medecin.getInfoUser().getMail());
            claims.put("confirmed", medecin.getInfoUser().isConfirmed());
            claims.put("isFirstAuth", medecin.getInfoUser().isFirstAuth());

            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .issuedAt(instant)
                    .expiresAt(instant.plus(30, ChronoUnit.MINUTES))
                    .subject(username)
                    .claim("claims", claims)
                    .build();

            JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS512).build(),
                    jwtClaimsSet
            );

            String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
            Map<String, String> response = new HashMap<>();
            response.put("access-token", jwt);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid username or password"));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // Log the exception and return a generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PostMapping("/professionelSante")
    public ResponseEntity<Map<String, String>> loginProfesionnelSante(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            Authentication authentication = authenticationManagerProfessionelSante.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            Instant instant = Instant.now();

            ProfessionnelSante professionnelSante = professionnelSanteRepository.findByCinOrMail(username)
                    .orElseThrow(() -> new UserNotFoundException("Professionnel not found with username: " + username));

            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("role", scope);

            claims.put("id", professionnelSante.getId());
            claims.put("nom", professionnelSante.getInfoUser().getNom());
            claims.put("prenom", professionnelSante.getInfoUser().getPrenom());
            claims.put("mail", professionnelSante.getInfoUser().getMail());
            claims.put("confirmed", professionnelSante.getInfoUser().isConfirmed());
            claims.put("isFirstAuth", professionnelSante.getInfoUser().isFirstAuth());

            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .issuedAt(instant)
                    .expiresAt(instant.plus(30, ChronoUnit.MINUTES))
                    .subject(username)
                    .claim("claims", claims)
                    .build();

            JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS512).build(),
                    jwtClaimsSet
            );

            String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
            Map<String, String> response = new HashMap<>();
            response.put("access-token", jwt);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid username or password"));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // Log the exception and return a generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    @PostMapping("/jeunes")
    public ResponseEntity<Map<String, String>> loginJeune(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            Authentication authentication = authenticationManagerJeune.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            Instant instant = Instant.now();

            Jeune jeune = jeuneRepo.findJeuneByMailOrCinOrCNEOrCodeMASSAR(username)
                    .orElseThrow(() -> new UserNotFoundException("Jeune not found with username: " + username));

            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("role", scope);

            claims.put("id", jeune.getId());
            claims.put("nom", jeune.getInfoUser().getNom());
            claims.put("prenom", jeune.getInfoUser().getPrenom());
            claims.put("mail", jeune.getInfoUser().getMail());
            claims.put("confirmed", jeune.getInfoUser().isConfirmed());
            claims.put("isFirstAuth", jeune.getInfoUser().isFirstAuth());

            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .issuedAt(instant)
                    .expiresAt(instant.plus(30, ChronoUnit.MINUTES))
                    .subject(username)
                    .claim("claims", claims)
                    .build();

            JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                    JwsHeader.with(MacAlgorithm.HS512).build(),
                    jwtClaimsSet
            );

            String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
            Map<String, String> response = new HashMap<>();
            response.put("access-token", jwt);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid username or password"));
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // Log the exception and return a generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }


}
