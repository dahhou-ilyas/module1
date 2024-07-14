package com.example.module1.mappers;



import com.example.module1.dto.ProfessionnelSanteResponseDTO;
import com.example.module1.entities.ProfessionnelSante;
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
