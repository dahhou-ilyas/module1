package com.example.module1.service;




import com.example.module1.dto.JeuneDto;
import com.example.module1.entities.AntecedentFamilial;
import com.example.module1.entities.AntecedentPersonnel;
import com.example.module1.entities.Jeune;
import com.example.module1.exception.EmailNonValideException;
import com.example.module1.exception.JeuneException;
import com.example.module1.exception.JeuneNotFoundException;
import com.example.module1.exception.PhoneNonValideException;
import org.apache.coyote.BadRequestException;

import java.util.Map;


public interface JeuneService {
    JeuneDto saveJeune(Jeune jeune) throws EmailNonValideException, PhoneNonValideException;
    AntecedentFamilial addAntecedentFamilial(Long jeuneId, AntecedentFamilial antecedentFamilial);
    AntecedentPersonnel addAntecedentPersonnel(Long jeuneId, AntecedentPersonnel antecedentPersonnel);
    Map<String, Object> getAntecedents(Long jeuneId) throws JeuneException;
    Object getJeuneById(Long id) throws JeuneNotFoundException;

    JeuneDto updateJeunePartial(Long id, Map<String, Object> updates) throws JeuneNotFoundException;

    Map<String, String> confirmAuthentification( Long id,String password) throws BadRequestException;
}