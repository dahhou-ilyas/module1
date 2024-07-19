package com.example.module1.securityConfig;

import com.example.module1.entities.Medecin;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.BadRequestException;
import com.example.module1.exception.UserNotFoundException;
import com.example.module1.repository.MedecinRepository;
import com.example.module1.repository.ProfessionnelSanteRepository;
import org.springframework.beans.factory.annotation.Qualifier;
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

    private final AuthenticationManager authenticationManagerMedecin;

    private final AuthenticationManager authenticationManagerProfessionelSante;

    public SecurityController(JwtEncoder jwtEncoder, MedecinRepository medecinRepository,ProfessionnelSanteRepository professionnelSanteRepository,
                              @Qualifier("authenticationManagerMedecin") AuthenticationManager authenticationManagerMedecin,
                              @Qualifier("authenticationManagerProfessionelSante") AuthenticationManager authenticationManagerProfessionelSante) {
        this.jwtEncoder = jwtEncoder;
        this.medecinRepository = medecinRepository;
        this.authenticationManagerMedecin = authenticationManagerMedecin;
        this.authenticationManagerProfessionelSante = authenticationManagerProfessionelSante;
        this.professionnelSanteRepository=professionnelSanteRepository;
    }

    @PostMapping("/medecins")
    public Map<String, String> loginMedcin(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            Authentication authentication = authenticationManagerMedecin.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            Instant instant = Instant.now();

            Medecin medecin = medecinRepository.findByCinOrMail(username)
                    .orElseThrow(() -> new UserNotFoundException("Medecin not found with username: " + username));

            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("role", scope);

            claims.put("id", medecin.getId());
            claims.put("nom", medecin.getAppUser().getNom());
            claims.put("prenom", medecin.getAppUser().getPrenom());
            claims.put("mail", medecin.getAppUser().getMail());
            claims.put("confirmed", medecin.getAppUser().isConfirmed());
            claims.put("isFirstAuth", medecin.getAppUser().getIsFirstAuth());

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
            return Map.of("access-token", jwt);
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Invalid username or password");
        }
    }

    @PostMapping("/professionelSante")
    public Map<String, String> loginProfesionnelSante(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            Authentication authentication = authenticationManagerProfessionelSante.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            Instant instant = Instant.now();

            ProfessionnelSante professionnelSante = professionnelSanteRepository.findByCinOrMail(username)
                    .orElseThrow(() -> new UserNotFoundException("Medecin not found with username: " + username));

            String scope = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(" "));

            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("role", scope);

            claims.put("id", professionnelSante.getId());
            claims.put("nom", professionnelSante.getUser().getNom());
            claims.put("prenom", professionnelSante.getUser().getPrenom());
            claims.put("mail", professionnelSante.getUser().getMail());
            claims.put("confirmed", professionnelSante.getUser().isConfirmed());
            claims.put("isFirstAuth", professionnelSante.getUser().getIsFirstAuth());

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
            return Map.of("access-token", jwt);
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Invalid username or password");
        }
    }


}
