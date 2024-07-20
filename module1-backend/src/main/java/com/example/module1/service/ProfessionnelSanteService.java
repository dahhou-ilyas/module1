package com.example.module1.service;

import com.example.module1.dto.ProfessionnelSanteResponseDTO;
import com.example.module1.entities.ProfessionnelSante;
import com.example.module1.exception.ProfessionnelSanteException;
import com.example.module1.exception.ProfessionnelSanteNotFoundException;

import java.util.List;
import java.util.Map;

public interface ProfessionnelSanteService{
    ProfessionnelSanteResponseDTO saveProfessionnelSante(ProfessionnelSante professionnelSante) throws ProfessionnelSanteException;

    ProfessionnelSanteResponseDTO getProfessionnelSanteById(Long id) throws ProfessionnelSanteNotFoundException;

    ProfessionnelSanteResponseDTO updateProfessionnelSantePartial(Long id, Map<String, Object> updates) throws ProfessionnelSanteNotFoundException;

    List<ProfessionnelSanteResponseDTO> getAllProfessionnelsSante();

    void deleteProfessionnelSante(Long id) throws ProfessionnelSanteNotFoundException;

}
