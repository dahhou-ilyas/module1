package com.example.module1.controller;

import com.example.module1.dto.ProfessionnelSanteResponseDTO;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.ProfessionnelSanteException;
import com.example.module1.exception.ProfessionnelSanteNotFoundException;
import com.example.module1.service.ProfessionnelSanteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
public class ProfessionnelSanteController {

    private final ProfessionnelSanteService professionnelSanteService;

    @PostMapping("/register/professionnelsantes")
    public ResponseEntity<ProfessionnelSanteResponseDTO> registerProfessionnelSante(@RequestBody ProfessionnelSante professionnelSante) throws ProfessionnelSanteException, ProfessionnelSanteException {
        ProfessionnelSanteResponseDTO savedProfessionnelSante = professionnelSanteService.saveProfessionnelSante(professionnelSante);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProfessionnelSante);
    }

    @PatchMapping("/professionnelsantes/{id}")
    public ResponseEntity<ProfessionnelSanteResponseDTO> patchProfessionnelSante(@PathVariable Long id, @RequestBody Map<String, Object> updates) throws ProfessionnelSanteNotFoundException, ProfessionnelSanteNotFoundException {
        ProfessionnelSanteResponseDTO updatedProfessionnelSante = professionnelSanteService.updateProfessionnelSantePartial(id, updates);
        return ResponseEntity.ok(updatedProfessionnelSante);
    }

    @GetMapping("/professionnelsantes")
    public ResponseEntity<List<ProfessionnelSanteResponseDTO>> getAllProfessionnelsSante() {
        try {
            List<ProfessionnelSanteResponseDTO> professionnelsSante = professionnelSanteService.getAllProfessionnelsSante();
            return ResponseEntity.ok(professionnelsSante);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/professionnelsantes/{id}")
    public ResponseEntity<String> deleteProfessionnelSante(@PathVariable Long id) {
        try {
            professionnelSanteService.deleteProfessionnelSante(id);
            return ResponseEntity.ok("Professionnel de santé supprimé avec succès");
        } catch (ProfessionnelSanteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/professionnelsantes/{id}")
    public ResponseEntity<ProfessionnelSanteResponseDTO> getProfessionnelSanteById(@PathVariable Long id) {
        try {
            ProfessionnelSanteResponseDTO professionnelSante = professionnelSanteService.getProfessionnelSanteById(id);
            return ResponseEntity.ok(professionnelSante);
        } catch (ProfessionnelSanteNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/register/professionnelsante/confirmation")
    public RedirectView confirmEmail(@RequestParam("token") String token) {
        ProfessionnelSante professionnelSante = professionnelSanteService.confirmEmail(token);
        return new RedirectView("https://www.youtube.com/watch?v=VIDEO_ID");
    }

    @ExceptionHandler(ProfessionnelSanteException.class)
    public ResponseEntity<Object> handleProfessionnelSanteException(ProfessionnelSanteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
