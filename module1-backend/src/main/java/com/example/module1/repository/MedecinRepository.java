package com.example.module1.repository;


import com.example.module1.entities.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedecinRepository extends JpaRepository<Medecin,Long> {
    boolean existsByCin(String cin);
    boolean existsByInpe(String inpe);
    boolean existsByPpr(String ppr);
}
