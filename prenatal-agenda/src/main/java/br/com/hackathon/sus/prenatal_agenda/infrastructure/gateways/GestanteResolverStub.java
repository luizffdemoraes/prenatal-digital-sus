package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.gateways.GestanteResolver;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Stub para desenvolvimento. Em produção, trocar por implementação que chama
 * o serviço de pacientes/usuários (ex.: por CPF ou email).
 * <p>
 * Mapeamento inicial: CPF 111.111.111-11 ou 11111111111 → 1; email gestante@exemplo.com → 1.
 */
@Component
public class GestanteResolverStub implements GestanteResolver {

    private static final Map<String, Long> POR_CPF = Map.ofEntries(
            Map.entry("11111111111", 1L),
            Map.entry("111.111.111-11", 1L)
    );

    private static final Map<String, Long> POR_EMAIL = Map.of(
            "gestante@exemplo.com", 1L
    );

    @Override
    public Optional<Long> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) return Optional.empty();
        return Optional.ofNullable(POR_CPF.get(cpf.trim()));
    }

    @Override
    public Optional<Long> buscarPorEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        return Optional.ofNullable(POR_EMAIL.get(email.trim().toLowerCase()));
    }
}
