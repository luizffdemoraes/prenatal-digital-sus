package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;

import java.util.Optional;

/**
 * Gateway (porta) para acesso aos dados de AgendaMedico
 * Interface do domínio que será implementada na camada de infraestrutura
 */
public interface AgendaMedicoGateway {

    AgendaMedico salvar(AgendaMedico agenda);

    Optional<AgendaMedico> buscarPorId(Long id);

    Optional<AgendaMedico> buscarPorMedicoId(Long medicoId);

    void excluirPorId(Long id);
}
