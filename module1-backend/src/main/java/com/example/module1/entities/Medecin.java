package com.example.module1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor
public class Medecin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "app_user_id", referencedColumnName = "id")
    private AppUser appUser;

    @Column(unique = true)
    private String cin ;
    @Column(unique = true)
    private String inpe;
    @Column(unique = true)
    private String ppr;

    private Boolean estMedcinESJ;
    private Boolean estGeneraliste;
    private String specialite;
    private String ROLE="MEDECIN";
    private boolean confirmed =false;
}
