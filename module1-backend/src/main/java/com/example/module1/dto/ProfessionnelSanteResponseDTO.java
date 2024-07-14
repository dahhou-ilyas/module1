package com.example.module1.dto;

import lombok.Data;

@Data
public class ProfessionnelSanteResponseDTO {

    private Long id;
    private String cin;
    private String nom;
    private String prenom;
    private String mail;
    private String inpe;

}