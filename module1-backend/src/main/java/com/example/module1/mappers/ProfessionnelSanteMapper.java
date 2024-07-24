package com.example.module1.mappers;



import com.example.module1.dto.ProfessionnelSanteResponseDTO;
import com.example.module1.entities.ProfessionnelSante;
import org.springframework.stereotype.Service;

@Service
public class ProfessionnelSanteMapper {
    public ProfessionnelSanteResponseDTO fromProfessionnelSante(ProfessionnelSante professionnelSante){
        ProfessionnelSanteResponseDTO professionnelSanteResponseDTO=new ProfessionnelSanteResponseDTO();
        professionnelSanteResponseDTO.setId(professionnelSante.getId());
        professionnelSanteResponseDTO.setPrenom(professionnelSante.getInfoUser().getPrenom());
        professionnelSanteResponseDTO.setNom(professionnelSante.getInfoUser().getNom());
        professionnelSanteResponseDTO.setMail(professionnelSante.getInfoUser().getMail());
        professionnelSanteResponseDTO.setCin(professionnelSante.getCin());
        professionnelSanteResponseDTO.setInpe(professionnelSante.getInpe());
        return professionnelSanteResponseDTO;
    }
}
