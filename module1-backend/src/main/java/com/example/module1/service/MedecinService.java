package com.example.module1.service;


import com.example.module1.dto.MedecinResponseDTO;
import com.example.module1.entities.Medecin;
import com.example.module1.exception.MedecinException;
import com.example.module1.exception.MedecinNotFoundException;

import java.util.Map;

public interface MedecinService extends ConfirmeMailService<Medecin> {
    MedecinResponseDTO saveMecine(Medecin medecin) throws MedecinException;

    MedecinResponseDTO getMedecinById(Long id) throws MedecinNotFoundException;

    public MedecinResponseDTO updateMedecinPartial(Long id, Map<String, Object> updates) throws MedecinNotFoundException;

    void deleteMedecin(Long id) throws MedecinNotFoundException, MedecinException;
}
