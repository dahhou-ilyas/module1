package com.example.module1.repository;


import com.example.module1.entities.Jeune;
import com.example.module1.entities.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JeuneRepo extends JpaRepository<Jeune, Long> {
    @Query("SELECT j FROM Jeune j " + "WHERE j.infoUser.mail = :searchParam " + "OR j.cin = :searchParam " +
            "OR EXISTS (SELECT s FROM JeuneScolarise s WHERE (s.CNE = :searchParam OR s.codeMASSAR = :searchParam) AND s.id = j.id)")
    Optional<Jeune> findJeuneByMailOrCinOrCNEOrCodeMASSAR(@Param("searchParam") String searchParam);

    @Query("SELECT j FROM Jeune j WHERE j.infoUser.mail = :mail")
    Optional<Jeune> findByMail(@Param("mail") String mail);
}
