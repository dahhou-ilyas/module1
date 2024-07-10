package com.example.module1.mappers;

import com.example.module1.dto.MedecinResponseDTO;
import com.example.module1.entities.Medecin;
import org.springframework.stereotype.Service;

@Service
public class MedecineMapper {
    public MedecinResponseDTO fromMedcine(Medecin medecin){
        MedecinResponseDTO medecinResponseDTO=new MedecinResponseDTO();
        medecinResponseDTO.setId(medecin.getId());
        medecinResponseDTO.setNom(medecin.getAppUser().getNom());
        medecinResponseDTO.setPrenom(medecin.getAppUser().getPrenom());
        medecinResponseDTO.setMail(medecin.getAppUser().getMail());
        medecinResponseDTO.setCin(medecin.getCin());
        medecinResponseDTO.setInpe(medecin.getInpe());
        medecinResponseDTO.setPpr(medecin.getPpr());
        medecinResponseDTO.setEstMedcinESJ(medecin.getEstMedcinESJ());
        medecinResponseDTO.setEstGeneraliste(medecin.getEstGeneraliste());
        medecinResponseDTO.setSpecialite(medecin.getSpecialite());
        return medecinResponseDTO;
    }
}
