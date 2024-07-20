package com.example.module1.dto;



import com.example.module1.enums.NiveauEtudes;
import com.example.module1.enums.Sexe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class JeuneNonScolariseDto {
    private Long id;
    private String nom;
    private String prenom;
    private Sexe sexe;
    private Date dateNaissance;
    private int age;
    private int identifiantPatient;
    private String cin;
    private String mail;
    private String numTele;
    private boolean isConfirmed;
    private boolean isFirstAuth;
    private String ROLE;
    private NiveauEtudes dernierNiveauEtudes;
    private boolean enActivite;
}
