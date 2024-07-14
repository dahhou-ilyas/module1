package backend.authModule.service;

import backend.authModule.dto.ProfessionnelSanteResponseDTO;
import backend.authModule.entities.AppUser;
import backend.authModule.entities.ProfessionnelSante;
import backend.authModule.entities.VerificationToken;
import backend.authModule.exception.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProfessionnelSanteService {
    ProfessionnelSanteResponseDTO saveProfessionnelSante(ProfessionnelSante professionnelSante) throws ProfessionnelSanteException;

    ProfessionnelSanteResponseDTO getProfessionnelSanteById(Long id) throws ProfessionnelSanteNotFoundException;

    ProfessionnelSanteResponseDTO updateProfessionnelSantePartial(Long id, Map<String, Object> updates) throws ProfessionnelSanteNotFoundException;

    List<ProfessionnelSanteResponseDTO> getAllProfessionnelsSante();

    void deleteProfessionnelSante(Long id) throws ProfessionnelSanteNotFoundException;

    ProfessionnelSante confirmEmail(String token);

    void sendEmail(String to, String subject, String htmlBody);
}
