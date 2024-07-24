package com.example.module1.controller;

import com.example.module1.repository.JeuneRepo;
import com.example.module1.repository.MedecinRepository;
import com.example.module1.repository.ProfessionnelSanteRepository;
import com.example.module1.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/validator")
@AllArgsConstructor
public class ValidatorController {

    private final UserRepository infoUserRepository;
    private final JeuneRepo jeuneRepository;
    private final MedecinRepository medecinRepository;
    private final ProfessionnelSanteRepository professionnelSanteRepository;


    @GetMapping("/infouser")
    public ResponseEntity<String> validateInfoUser(
            @RequestParam(required = false) String mail,
            @RequestParam(required = false) String numTel) {

        boolean mailExists = mail != null && infoUserRepository.existsByMail(mail);
        boolean numTelExists = numTel != null && infoUserRepository.existsByNumTel(numTel);

        if ((!mailExists && mail != null) || (!numTelExists && numTel != null)) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Mail or numTel already exists");
        }
    }

    @GetMapping("/cin")
    public ResponseEntity<String> validateCin(@RequestParam String cin) {
        boolean existsInJeune = jeuneRepository.existsByCin(cin);
        boolean existsInMedecin = medecinRepository.existsByCin(cin);
        boolean existsInProfessionnelSante = professionnelSanteRepository.existsByCin(cin);

        if (!existsInJeune && !existsInMedecin && !existsInProfessionnelSante) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("CIN already exists");
        }
    }

    @GetMapping("/inpe")
    public ResponseEntity<String> validateInpe(@RequestParam String inpe) {
        boolean existsInMedecin = medecinRepository.existsByInpe(inpe);
        boolean existsInProfessionnelSante = professionnelSanteRepository.existsByInpe(inpe);

        if (existsInMedecin || existsInProfessionnelSante) {
            return ResponseEntity.ok("INPE already exists");
        } else {
            return ResponseEntity.ok("OK");
        }
    }

    @GetMapping("/ppr")
    public ResponseEntity<String> validatePpr(@RequestParam(required = false) String ppr) {
        if (ppr == null || ppr.isEmpty()) {
            return ResponseEntity.badRequest().body("PPR is required");
        }

        boolean existsInMedecin = medecinRepository.existsByPpr(ppr);

        if (existsInMedecin) {
            return ResponseEntity.ok("PPR already exists");
        } else {
            return ResponseEntity.ok("OK");
        }
    }
    @GetMapping("/cne")
    public ResponseEntity<String> validateCne(@RequestParam String cne) {
        boolean existsInJeuneCne = jeuneRepository.existsByCNE(cne);

        if (existsInJeuneCne) {
            return ResponseEntity.ok("CNE already exists");
        } else {
            return ResponseEntity.ok("OK");
        }
    }

    @GetMapping("/codeMassare")
    public ResponseEntity<String> validateCodeMassare(@RequestParam String codeMassare) {
        boolean existsInJeuneCodeMassare= jeuneRepository.existsByCodeMassare(codeMassare);

        if (existsInJeuneCodeMassare) {
            return ResponseEntity.ok("Code Massare already exists");
        } else {
            return ResponseEntity.ok("OK");
        }
    }

}
