package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

import java.util.Optional;

/**
 * Resolve nome, especialidade ou CRM do médico para o ID usado no agendamento.
 * Em produção, implementação chama o serviço de profissionais de saúde.
 */
public interface MedicoResolver {

    Optional<Long> buscarPorCrm(String crm);

    Optional<Long> buscarPorNome(String nome, Long unidadeId);

    Optional<Long> buscarPorEspecialidade(String especialidade, Long unidadeId);
}
