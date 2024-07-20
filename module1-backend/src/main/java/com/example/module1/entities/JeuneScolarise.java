package com.example.module1.entities;


import com.example.module1.enums.NiveauEtudes;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SC")
@Data @AllArgsConstructor @NoArgsConstructor
public class JeuneScolarise extends Jeune{
    @Enumerated(EnumType.STRING)
    private NiveauEtudes niveauEtudesActuel;
    @Column(unique = true)
    private String CNE;
    @Column(unique = true)
    private String codeMASSAR;
}
