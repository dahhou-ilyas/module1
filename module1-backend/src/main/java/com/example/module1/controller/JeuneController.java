package com.example.module1.controller;


import com.example.module1.dto.JeuneDto;
import com.example.module1.entities.AntecedentFamilial;
import com.example.module1.entities.AntecedentPersonnel;
import com.example.module1.entities.JeuneNonScolarise;
import com.example.module1.entities.JeuneScolarise;
import com.example.module1.exception.EmailNonValideException;
import com.example.module1.exception.JeuneException;
import com.example.module1.exception.JeuneNotFoundException;
import com.example.module1.exception.PhoneNonValideException;
import com.example.module1.service.JeuneService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.logging.Logger;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class JeuneController {
    private static final Logger logger = Logger.getLogger(JeuneController.class.getName());
    private JeuneService jeuneService;

    @GetMapping("/jeunes/{id}")
    public ResponseEntity<?> getJeuneById(@PathVariable(value = "id") Long id) {
        try {
            Object jeune = jeuneService.getJeuneById(id);
            return ResponseEntity.ok().body(jeune);
        } catch (JeuneNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/register/jeunes/scolarise")
    public ResponseEntity<JeuneDto> saveJeuneScolarise(@RequestBody JeuneScolarise jeuneScolarise) throws EmailNonValideException, PhoneNonValideException {

        JeuneDto savedJeune = jeuneService.saveJeune(jeuneScolarise);
        return ResponseEntity.ok(savedJeune);

    }

    @PostMapping("/register/jeunes/nonscolarise")
    public ResponseEntity<JeuneDto> saveJeuneNonScolarise(@RequestBody JeuneNonScolarise jeuneNonScolarise) {
        try {
            JeuneDto savedJeune = jeuneService.saveJeune(jeuneNonScolarise);
            return ResponseEntity.ok(savedJeune);
        } catch (EmailNonValideException | PhoneNonValideException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/jeunes/{jeuneId}/antecedents/familiaux")
    public ResponseEntity<?> addAntecedentFamilial(@PathVariable Long jeuneId, @RequestBody AntecedentFamilial antecedentFamilial) {
        try {
            logger.info("Received request to add antecedent familial for jeuneId: " + jeuneId);
            AntecedentFamilial savedAntecedentFamilial = jeuneService.addAntecedentFamilial(jeuneId, antecedentFamilial);
            logger.info("Successfully added antecedent familial for jeuneId: " + jeuneId);
            return ResponseEntity.ok(savedAntecedentFamilial);
        } catch (IllegalArgumentException e) {
            logger.warning("Failed to add antecedent familial: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/jeunes/{jeuneId}/antecedents/personnels")
    public ResponseEntity<?> addAntecedentPersonnel(@PathVariable Long jeuneId, @RequestBody AntecedentPersonnel antecedentPersonnel) {
         try {
                 logger.info("Received request to add antecedent personnel for jeuneId: " + jeuneId);
                  AntecedentPersonnel savedAntecedentPersonnel = jeuneService.addAntecedentPersonnel(jeuneId, antecedentPersonnel);
                  logger.info("Successfully added antecedent personnel for jeuneId: " + jeuneId);
                  return ResponseEntity.ok(savedAntecedentPersonnel);
         } catch (IllegalArgumentException e) {
                    logger.warning("Failed to add antecedent personnel: " + e.getMessage());
                    return ResponseEntity.badRequest().body(e.getMessage());
         }
    }
    @GetMapping("/jeunes/{jeuneId}/antecedents")
    public ResponseEntity<?> getAntecedents(@PathVariable Long jeuneId) {
        try {
            logger.info("Received request to get antecedents for jeuneId: " + jeuneId);
            Map<String, Object> result = jeuneService.getAntecedents(jeuneId);
            logger.info("Successfully retrieved antecedents for jeuneId: " + jeuneId);
            return ResponseEntity.ok(result);
        } catch (JeuneException e) {
            logger.warning("Failed to retrieve antecedents: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/jeunes/{id}")
    public ResponseEntity<?> patchMedecin(@PathVariable Long id, @RequestBody Map<String, Object> updates)  {
        try {
            JeuneDto updateJeunePartial = jeuneService.updateJeunePartial(id, updates);
            return ResponseEntity.ok(updateJeunePartial);
        }catch (JeuneNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/jeunes/confirm-Fisrtauth/{id}")
    public ResponseEntity<Map<String, String>> confirmAuthentification(@PathVariable Long id,@RequestBody Map<String, String> details) {
        try {
            String password=details.get("password");
            // Appeler le service pour confirmer l'authentification et obtenir le nouveau token
            Map<String, String> response = jeuneService.confirmAuthentification(id,password);

            // Retourner le token dans la réponse
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            // Retourner une réponse d'erreur si quelque chose échoue
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}