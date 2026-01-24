package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.gateways.UnidadeResolver;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Stub para desenvolvimento. Em produção, trocar por implementação que chama
 * o serviço de unidades/UBS.
 * <p>
 * Mapeamento: "UBS Centro", "UBS Centro" (normalizado) → 1.
 */
@Component
public class UnidadeResolverStub implements UnidadeResolver {

    private static final Map<String, Long> POR_NOME = Map.ofEntries(
            Map.entry("ubs centro", 1L),
            Map.entry("centro", 1L),
            Map.entry("ubs 1", 1L)
    );

    @Override
    public Optional<Long> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) return Optional.empty();
        return Optional.ofNullable(POR_NOME.get(nome.trim().toLowerCase()));
    }
}
