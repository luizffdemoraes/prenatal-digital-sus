package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

import java.util.Optional;

public interface PatientGateway {

    Optional<Long> buscarPorCpf(String cpf);
}
