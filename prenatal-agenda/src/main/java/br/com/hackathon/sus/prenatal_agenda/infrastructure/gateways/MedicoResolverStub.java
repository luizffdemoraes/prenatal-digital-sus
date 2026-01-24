package br.com.hackathon.sus.prenatal_agenda.infrastructure.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.gateways.MedicoResolver;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Stub para desenvolvimento. Em produção, trocar por implementação que chama
 * o serviço de profissionais (ex.: por nome ou especialidade).
 * <p>
 * Mapeamento: "Dr. João", "Dr. Silva", "Obstetrícia", "OBSTETRICA" → 1.
 */
@Component
public class MedicoResolverStub implements MedicoResolver {

    private static final Map<String, Long> POR_NOME = Map.ofEntries(
            Map.entry("dr. joão", 1L),
            Map.entry("dr joão", 1L),
            Map.entry("joão", 1L),
            Map.entry("dr. silva", 1L),
            Map.entry("dr silva", 1L),
            Map.entry("silva", 1L)
    );

    private static final Map<String, Long> POR_ESPECIALIDADE = Map.ofEntries(
            Map.entry("obstetrícia", 1L),
            Map.entry("obstetricia", 1L),
            Map.entry("obstetra", 1L),
            Map.entry("prenatal", 1L)
    );

    @Override
    public Optional<Long> buscarPorNome(String nome, Long unidadeId) {
        if (nome == null || nome.isBlank()) return Optional.empty();
        return Optional.ofNullable(POR_NOME.get(nome.trim().toLowerCase()));
    }

    @Override
    public Optional<Long> buscarPorEspecialidade(String especialidade, Long unidadeId) {
        if (especialidade == null || especialidade.isBlank()) return Optional.empty();
        return Optional.ofNullable(POR_ESPECIALIDADE.get(especialidade.trim().toLowerCase()));
    }
}
