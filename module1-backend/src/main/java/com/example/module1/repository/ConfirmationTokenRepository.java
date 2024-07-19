package com.example.module1.repository;


import com.example.module1.entities.ConfirmationToken;
import com.example.module1.entities.Medecin;
import com.example.module1.entities.ProfessionnelSante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long> {
    ConfirmationToken findByToken(String token);
    ConfirmationToken findByMedecin(Medecin medecin);
    ConfirmationToken findByProfessionnelSante(ProfessionnelSante professionnelSante);
}
