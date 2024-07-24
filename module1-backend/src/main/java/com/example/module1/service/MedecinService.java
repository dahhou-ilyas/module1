package com.example.module1.service;


import com.example.module1.dto.MedecinResponseDTO;
import com.example.module1.entities.Medecin;
import com.example.module1.exception.MedecinException;
import com.example.module1.exception.MedecinNotFoundException;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Map;

public interface MedecinService {
    MedecinResponseDTO saveMedecin(Medecin medecin) throws MedecinException;

    MedecinResponseDTO getMedecinById(Long id) throws MedecinNotFoundException;

    MedecinResponseDTO updateMedecinPartial(Long id, Map<String, Object> updates) throws MedecinNotFoundException;

    void deleteMedecin(Long id) throws MedecinNotFoundException, MedecinException;

    List<MedecinResponseDTO> getAllMedecins();

    Map<String, String> confirmAuthentification(Long id,String password) throws BadRequestException;
}
