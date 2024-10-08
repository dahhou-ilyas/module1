package com.example.module1.mappers;



import com.example.module1.dto.JeuneScolariseDto;
import com.example.module1.entities.InfoUser;
import com.example.module1.entities.JeuneScolarise;
import org.springframework.stereotype.Service;

@Service
public class JeuneScolariseMapper {
    public JeuneScolariseDto toDtoJeuneS(JeuneScolarise jeuneScolarise) {
        JeuneScolariseDto jeuneScolariseDto = new JeuneScolariseDto();
        InfoUser infoUser = jeuneScolarise.getInfoUser();
        jeuneScolariseDto.setId(jeuneScolarise.getId());
        jeuneScolariseDto.setPrenom(infoUser.getPrenom());
        jeuneScolariseDto.setNom(infoUser.getNom());
        jeuneScolariseDto.setMail(infoUser.getMail());
        jeuneScolariseDto.setNumTele(infoUser.getNumTel());
        jeuneScolariseDto.setSexe(jeuneScolarise.getSexe());
        jeuneScolariseDto.setDateNaissance(jeuneScolarise.getDateNaissance());
        jeuneScolariseDto.setAge(jeuneScolarise.getAge());
        jeuneScolariseDto.setIdentifiantPatient(jeuneScolarise.getIdentifiantPatient());
        jeuneScolariseDto.setCin(jeuneScolarise.getCin());
        jeuneScolariseDto.setConfirmed(jeuneScolarise.getInfoUser().isConfirmed());
        jeuneScolariseDto.setFirstAuth(jeuneScolarise.getInfoUser().isFirstAuth());
        jeuneScolariseDto.setROLE(jeuneScolarise.getROLE());
        jeuneScolariseDto.setNiveauEtudesActuel(jeuneScolarise.getNiveauEtudesActuel());
        jeuneScolariseDto.setCNE(jeuneScolarise.getCNE());
        jeuneScolariseDto.setCodeMASSAR(jeuneScolarise.getCodeMassare());

        return jeuneScolariseDto;
    }
}

