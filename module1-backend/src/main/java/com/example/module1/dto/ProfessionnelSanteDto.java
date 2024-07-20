package com.example.module1.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfessionnelSanteDto {
    private Long id;
    private String nom;
    private String prenom;
    private String mail;
    private String cin;
    private String inpe;
    private String numTele;
}
