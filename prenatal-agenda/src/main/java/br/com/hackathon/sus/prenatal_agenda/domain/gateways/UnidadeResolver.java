package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

import java.util.Optional;

/**
 * Resolve nome da unidade de saúde para o ID usado no agendamento.
 * Em produção, implementação chama o serviço de unidades/UBS.
 */
public interface UnidadeResolver {

    Optional<Long> buscarPorNome(String nome);
}
