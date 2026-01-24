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

    private static final Map<Long, String> ID_TO_NAME = Map.ofEntries(
            Map.entry(1L, "Maria"),
            Map.entry(2L, "Ana"),
            Map.entry(5L, "Joana"),
            Map.entry(10L, "Maria")
    );

    @Override
    public Optional<Long> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return Optional.empty();
        return Optional.ofNullable(POR_CPF.get(cpf.trim().replaceAll("\\D", "")));
    }

    @Override
    public Optional<String> findNameById(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(ID_TO_NAME.get(id));
    }
}
