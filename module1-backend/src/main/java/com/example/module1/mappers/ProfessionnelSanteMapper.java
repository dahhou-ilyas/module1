package backend.authModule.mappers;

import backend.authModule.dto.MedecinResponseDTO;
import backend.authModule.dto.ProfessionnelSanteResponseDTO;
import backend.authModule.entities.Medecin;
import backend.authModule.entities.ProfessionnelSante;
import org.springframework.stereotype.Service;

@Service
public class ProfessionnelSanteMapper {
    public ProfessionnelSanteResponseDTO fromProfessionnelSante(ProfessionnelSante professionnelSante){
        ProfessionnelSanteResponseDTO professionnelSanteResponseDTO=new ProfessionnelSanteResponseDTO();
        professionnelSanteResponseDTO.setId(professionnelSante.getId());
        professionnelSanteResponseDTO.setPrenom(professionnelSante.getUser().getPrenom());
        professionnelSanteResponseDTO.setNom(professionnelSante.getUser().getNom());
        professionnelSanteResponseDTO.setMail(professionnelSante.getUser().getMail());
        professionnelSanteResponseDTO.setCin(professionnelSante.getCin());
        professionnelSanteResponseDTO.setInpe(professionnelSante.getInpe());
        return professionnelSanteResponseDTO;
    }
}
