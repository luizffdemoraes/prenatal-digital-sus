package br.com.hackathon.sus.prenatal_ia.domain.repositories;

import br.com.hackathon.sus.prenatal_ia.domain.entities.PregnantPatient;

import java.util.List;

public interface ProntuarioRepository {
    List<PregnantPatient> findAllActivePregnancies();
}
