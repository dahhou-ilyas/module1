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
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/medecins")
@AllArgsConstructor
public class MedecinController {
    private MedecinService medecinService;

    @PostMapping
    public MedecinResponseDTO createMedcine(@RequestBody Medecin medecin) throws MedecinException {
        return medecinService.saveMecine(medecin);
    }

    @DeleteMapping("/{id}")
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
    @GetMapping("/{id}")
    public ResponseEntity<MedecinResponseDTO> getMedecinById(@PathVariable Long id) {
        try {
            MedecinResponseDTO medecin = medecinService.getMedecinById(id);
            return ResponseEntity.ok(medecin);
        } catch (MedecinNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }



    @GetMapping("/confirmation")
    public RedirectView confirmEmail(@RequestParam("token") String token) {

        Medecin medecin = medecinService.confirmEmail(token);

        return new RedirectView("https://www.youtube.com/watch?v=VIDEO_ID");
    }

    @ExceptionHandler(MedecinException.class)
    public ResponseEntity<Object> handleMedecinException(MedecinException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}

