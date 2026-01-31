package br.com.hackathon.sus.prenatal_ia.domain.gateways;

import br.com.hackathon.sus.prenatal_ia.domain.entities.PregnantPatient;

import java.util.List;

public interface ProntuarioGateway {
    List<PregnantPatient> findAllActivePregnancies();
}
