package com.example.module1.repository;



import com.example.module1.entities.AntecedentPersonnel;
import com.example.module1.entities.Jeune;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AntecedentPersonnelRepo extends JpaRepository<AntecedentPersonnel,Long> {
    Optional<AntecedentPersonnel> findByJeune(Jeune jeune);
}
