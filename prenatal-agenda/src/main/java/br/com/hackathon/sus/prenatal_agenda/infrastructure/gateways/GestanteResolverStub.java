package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.gateways.GestanteResolver;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Stub para desenvolvimento. Em produção, trocar por implementação que chama
 * o serviço de pacientes/usuários por CPF.
 * <p>
 * Mapeamento: CPF 111.111.111-11 ou 11111111111 → 1.
 */
@Component
public class GestanteResolverStub implements GestanteResolver {

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
