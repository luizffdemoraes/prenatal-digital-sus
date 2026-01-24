package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

import java.util.Optional;

/**
 * Resolve identificadores da gestante (CPF, email) para o ID usado no agendamento.
 * Em produção, implementação chama o serviço de pacientes/usuários.
 */
public interface GestanteResolver {

    Optional<Long> buscarPorCpf(String cpf);

    Optional<Long> buscarPorEmail(String email);
}
