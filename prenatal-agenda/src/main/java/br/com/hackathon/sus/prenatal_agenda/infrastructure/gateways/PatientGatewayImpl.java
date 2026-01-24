package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.gateways.PatientGateway;

import java.util.Map;
import java.util.Optional;

/**
 * Stub implementation for development. Replace with patient/user service in production.
 */
public class PatientGatewayImpl implements PatientGateway {

    private static final Map<String, Long> POR_CPF = Map.ofEntries(
            Map.entry("11111111111", 1L),
            Map.entry("111.111.111-11", 1L)
    );

    @Override
    public Optional<Long> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return Optional.empty();
        return Optional.ofNullable(POR_CPF.get(cpf.trim().replaceAll("\\D", "")));
    }
}
