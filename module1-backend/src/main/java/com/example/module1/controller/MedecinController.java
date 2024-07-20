package com.example.module1.controller;

import com.example.module1.dto.MedecinResponseDTO;
import com.example.module1.entities.Medecin;
import com.example.module1.exception.MedecinException;
import com.example.module1.exception.MedecinNotFoundException;
import com.example.module1.service.MedecinService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController

@AllArgsConstructor
@CrossOrigin("*")
public class MedecinController {
    private MedecinService medecinService;

    @PostMapping("/register/medecins")
    public ResponseEntity<?> createMedecin(@RequestBody Medecin medecin) {
        try {
            MedecinResponseDTO responseDTO = medecinService.saveMedecin(medecin);
            return ResponseEntity.ok(responseDTO);
        } catch (MedecinException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur s'est produite lors du traitement de la requête");
        }
    }

    @DeleteMapping("/medecins/{id}")
    public ResponseEntity<String> deleteMedecin(@PathVariable Long id) {
        try {
            medecinService.deleteMedecin(id);
            return ResponseEntity.ok("Médecin supprimé avec succès");
        } catch (MedecinNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (MedecinException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    @GetMapping("/medecins/{id}")
    public ResponseEntity<MedecinResponseDTO> getMedecinById(@PathVariable Long id) {
        try {
            MedecinResponseDTO medecin = medecinService.getMedecinById(id);
            return ResponseEntity.ok(medecin);
        } catch (MedecinNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/medecins/{id}")
    public ResponseEntity<MedecinResponseDTO> patchMedecin(@PathVariable Long id, @RequestBody Map<String, Object> updates) throws MedecinNotFoundException {
        MedecinResponseDTO updatedMedecin = medecinService.updateMedecinPartial(id, updates);
        return ResponseEntity.ok(updatedMedecin);
    }

    @GetMapping("/medecins")
    public ResponseEntity<List<MedecinResponseDTO>> getAllMedecins() {
        try {
            List<MedecinResponseDTO> medecins = medecinService.getAllMedecins();
            return ResponseEntity.ok(medecins);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @ExceptionHandler(MedecinException.class)
    public ResponseEntity<Object> handleMedecinException(MedecinException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}

