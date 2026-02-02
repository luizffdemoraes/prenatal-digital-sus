package br.com.hackathon.sus.prenatal_alertas.domain.repositories;

import br.com.hackathon.sus.prenatal_alertas.domain.entities.PregnantPatient;

import java.util.List;

public interface ProntuarioRepository {
    List<PregnantPatient> findAllActivePregnancies();
}
