package com.leonardo.apprh.repository;

import java.util.List;

import com.leonardo.apprh.models.Candidato;
import com.leonardo.apprh.models.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidatoRepository extends JpaRepository<Candidato, Long> {

    List<Candidato>findByVaga(Vaga vaga);

    Candidato findByRg(String rg);

    Candidato findById(long id);

    List<Candidato>findByNomeCandidato(String nomeCandidato);
}