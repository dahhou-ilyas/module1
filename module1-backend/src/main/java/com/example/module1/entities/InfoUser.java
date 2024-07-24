package com.example.module1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class InfoUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    @Column(unique = true)
    private String mail;
    private String numTel;
    private String motDePasse;

    private boolean confirmed =false;
    private boolean isFirstAuth = true;


}
