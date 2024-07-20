package com.example.module1.repository;




import com.example.module1.entities.AntecedentFamilial;
import com.example.module1.entities.Jeune;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AntecedentFamilialRepo extends JpaRepository<AntecedentFamilial,Long> {
    Optional<AntecedentFamilial> findByJeune(Jeune jeune);
}
