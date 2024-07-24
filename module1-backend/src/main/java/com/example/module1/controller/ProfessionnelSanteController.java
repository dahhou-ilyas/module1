package com.example.module1.controller;

import com.example.module1.dto.ProfessionnelSanteResponseDTO;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.ProfessionnelSanteException;
import com.example.module1.exception.ProfessionnelSanteNotFoundException;
import com.example.module1.service.ProfessionnelSanteService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
public class ProfessionnelSanteController {

    private final ProfessionnelSanteService professionnelSanteService;

    @PostMapping("/register/professionnels")
    public ResponseEntity<ProfessionnelSanteResponseDTO> registerProfessionnelSante(@RequestBody ProfessionnelSante professionnelSante) throws ProfessionnelSanteException, ProfessionnelSanteException {
        ProfessionnelSanteResponseDTO savedProfessionnelSante = professionnelSanteService.saveProfessionnelSante(professionnelSante);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProfessionnelSante);
    }

    @PatchMapping("/professionnels/{id}")
    public ResponseEntity<ProfessionnelSanteResponseDTO> patchProfessionnelSante(@PathVariable Long id, @RequestBody Map<String, Object> updates) throws ProfessionnelSanteNotFoundException, ProfessionnelSanteNotFoundException {
        ProfessionnelSanteResponseDTO updatedProfessionnelSante = professionnelSanteService.updateProfessionnelSantePartial(id, updates);
        return ResponseEntity.ok(updatedProfessionnelSante);
    }

    @GetMapping("/professionnels")
    public ResponseEntity<List<ProfessionnelSanteResponseDTO>> getAllProfessionnelsSante() {
        try {
            List<ProfessionnelSanteResponseDTO> professionnelsSante = professionnelSanteService.getAllProfessionnelsSante();
            return ResponseEntity.ok(professionnelsSante);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/professionnels/{id}")
    public ResponseEntity<String> deleteProfessionnelSante(@PathVariable Long id) {
        try {
            professionnelSanteService.deleteProfessionnelSante(id);
            return ResponseEntity.ok("Professionnel de santé supprimé avec succès");
        } catch (ProfessionnelSanteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/professionnels/{id}")
    public ResponseEntity<ProfessionnelSanteResponseDTO> getProfessionnelSanteById(@PathVariable Long id) {
        try {
            ProfessionnelSanteResponseDTO professionnelSante = professionnelSanteService.getProfessionnelSanteById(id);
            return ResponseEntity.ok(professionnelSante);
        } catch (ProfessionnelSanteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/professionnels/confirm-Fisrtauth/{id}")
    public ResponseEntity<Map<String, String>> confirmAuthentification(@PathVariable Long id,@RequestBody Map<String, String> details) {
        try {
            String password=details.get("password");
            // Appeler le service pour confirmer l'authentification et obtenir le nouveau token
            Map<String, String> response = professionnelSanteService.confirmAuthentification(id,password);

            // Retourner le token dans la réponse
            return ResponseEntity.ok(response);
        } catch (BadRequestException e) {
            // Retourner une réponse d'erreur si quelque chose échoue
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @ExceptionHandler(ProfessionnelSanteException.class)
    public ResponseEntity<Object> handleProfessionnelSanteException(ProfessionnelSanteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
