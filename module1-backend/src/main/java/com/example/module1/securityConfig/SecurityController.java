package backend.authModule.securityConfig;

import backend.authModule.entities.Jeune;
import backend.authModule.entities.Medecin;
import backend.authModule.entities.ProfessionnelSante;
import backend.authModule.repository.JeuneRepository;
import backend.authModule.repository.MedecinRepository;
import backend.authModule.repository.ProfessionnelSanteRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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


    private JwtEncoder jwtEncoder;
    private JeuneRepository jeuneRepository;
    private MedecinRepository medecinRepository;
    @Autowired
    private ProfessionnelSanteRepository professionnelSanteRepository;

    private final AuthenticationManager authenticationManagerJeune;
    private final AuthenticationManager authenticationManagerMedecin;
    private final AuthenticationManager authenticationManagerProfessionnelSante;

    public SecurityController(JwtEncoder jwtEncoder, JeuneRepository jeuneRepository, MedecinRepository medecinRepository,
                              @Qualifier("authenticationManagerJeune") AuthenticationManager authenticationManagerJeune,
                              @Qualifier("authenticationManagerMedecin") AuthenticationManager authenticationManagerMedecin,
                             @Qualifier("authenticationManagerProfessionnelSante") AuthenticationManager authenticationManagerProfessionnelSante) {

            this.authenticationManagerJeune = authenticationManagerJeune;
        this.authenticationManagerMedecin = authenticationManagerMedecin;
        this.authenticationManagerProfessionnelSante = authenticationManagerProfessionnelSante;
        this.jwtEncoder = jwtEncoder;
        this.jeuneRepository = jeuneRepository;
        this.medecinRepository = medecinRepository;
        this.professionnelSanteRepository = professionnelSanteRepository;
    }



    @PostMapping("/jeunes")
    public Map<String, String> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Authentication authentication = authenticationManagerJeune.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        Instant instant = Instant.now();

        Jeune jeune = jeuneRepository.findByMailOrCinOrCNEOrCodeMASSAR(username).orElse(null);


        String scope = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.joining(" "));

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", scope);

        if (jeune != null) {
            claims.put("id", jeune.getId());
            claims.put("nom", jeune.getAppUser().getNom());
            claims.put("prenom", jeune.getAppUser().getPrenom());
            claims.put("mail", jeune.getAppUser().getMail());
            claims.put("confirmed",jeune.getIsConfirmed());
            claims.put("isFirstAuth",jeune.getIsFirstAuth());
        }

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(instant)
                .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                .subject(username)
                .claim("claims",claims)
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(),
                jwtClaimsSet
        );

        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return Map.of("access-token", jwt);
    }

    @PostMapping("/medecins")
    public Map<String, String> loginMedcin(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Authentication authentication = authenticationManagerMedecin.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        Instant instant = Instant.now();

        Medecin medecin = medecinRepository.findByCinOrMail(username).orElse(null);


        String scope = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.joining(" "));

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", scope);

        if (medecin != null) {
            claims.put("id", medecin.getId());
            claims.put("nom", medecin.getAppUser().getNom());
            claims.put("prenom", medecin.getAppUser().getPrenom());
            claims.put("mail", medecin.getAppUser().getMail());
            claims.put("confirmed",medecin.isConfirmed());
            claims.put("isFirstAuth",medecin.getIsFirstAuth());
        }

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(instant)
                .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                .subject(username)
                .claim("claims",claims)
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(),
                jwtClaimsSet
        );

        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return Map.of("access-token", jwt);
    }
    @PostMapping("/professionnelsantes")
    public Map<String, String> loginProfessionnelSante(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Authentication authentication = authenticationManagerProfessionnelSante.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        Instant instant = Instant.now();

        ProfessionnelSante professionnelSante = professionnelSanteRepository.findByCinOrMail(username).orElse(null);

        String scope = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.joining(" "));

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", scope);

        if (professionnelSante != null) {
            claims.put("id", professionnelSante.getId());
            claims.put("cin", professionnelSante.getCin());
            claims.put("inpe", professionnelSante.getInpe());
            claims.put("confirmed", professionnelSante.isConfirmed());
            claims.put("isFirstAuth", professionnelSante.getIsFirstAuth());
            // Ajoutez d'autres informations que vous souhaitez inclure dans le token
        }

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(instant)
                .expiresAt(instant.plus(10, ChronoUnit.MINUTES))
                .subject(username)
                .claim("claims", claims)
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(),
                jwtClaimsSet
        );

        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return Map.of("access-token", jwt);
    }


}
