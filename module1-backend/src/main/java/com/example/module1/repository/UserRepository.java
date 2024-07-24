package com.example.module1.repository;


import com.example.module1.entities.InfoUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<InfoUser, Long> {
    Optional<InfoUser> findByMail(String mail);


    boolean existsByMail(String mail);
    boolean existsByNumTel(String numTel);
}
