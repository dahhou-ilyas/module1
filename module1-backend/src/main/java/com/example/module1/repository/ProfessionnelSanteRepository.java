package com.example.module1.repository;

import com.example.module1.entities.ProfessionnelSante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessionnelSanteRepository extends JpaRepository<ProfessionnelSante, Long> {
    @Query("SELECT p FROM ProfessionnelSante p WHERE p.cin = :cin")
    Optional<ProfessionnelSante> findByCin(@Param("cin") String cin);

    @Query("SELECT p FROM ProfessionnelSante p WHERE p.infoUser.mail = :mail")
    Optional<ProfessionnelSante> findByMail(@Param("mail") String mail);

    @Query("SELECT p FROM ProfessionnelSante p WHERE p.cin = :recherche OR p.infoUser.mail = :recherche")
    Optional<ProfessionnelSante> findByCinOrMail(@Param("recherche") String recherche);

    boolean existsByCin(String cin);
    boolean existsByInpe(String inpe);
}

