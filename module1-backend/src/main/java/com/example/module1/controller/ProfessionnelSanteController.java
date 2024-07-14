package backend.authModule.web;

import backend.authModule.dto.ProfessionnelSanteResponseDTO;
import backend.authModule.entities.ProfessionnelSante;
import backend.authModule.exception.ProfessionnelSanteException;
import backend.authModule.exception.ProfessionnelSanteNotFoundException;
import backend.authModule.service.ProfessionnelSanteService;
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
    public ResponseEntity<ProfessionnelSanteResponseDTO> registerProfessionnelSante(@RequestBody ProfessionnelSante professionnelSante) throws ProfessionnelSanteException {
        ProfessionnelSanteResponseDTO savedProfessionnelSante = professionnelSanteService.saveProfessionnelSante(professionnelSante);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProfessionnelSante);
    }

    @PatchMapping("/professionnelsantes/{id}")
    public ResponseEntity<ProfessionnelSanteResponseDTO> patchProfessionnelSante(@PathVariable Long id, @RequestBody Map<String, Object> updates) throws ProfessionnelSanteNotFoundException {
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

    @GetMapping("/register/professionnelsantes/confirmation")
    public RedirectView confirmEmail(@RequestParam("token") String token) {
        ProfessionnelSante professionnelSante = professionnelSanteService.confirmEmail(token);
        return new RedirectView("https://www.youtube.com/watch?v=VIDEO_ID");
    }

    @ExceptionHandler(ProfessionnelSanteException.class)
    public ResponseEntity<Object> handleProfessionnelSanteException(ProfessionnelSanteException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
