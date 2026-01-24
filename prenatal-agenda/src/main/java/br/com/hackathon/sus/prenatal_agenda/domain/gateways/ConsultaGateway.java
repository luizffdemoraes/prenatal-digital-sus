package br.com.hackathon.sus.prenatal_agenda.domain.gateways;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Gateway (porta) para acesso aos dados de Consulta
 * Interface do domínio que será implementada na camada de infraestrutura
 */
public interface ConsultaGateway {
    
    Consulta salvar(Consulta consulta);
    
    Optional<Consulta> buscarPorId(Long id);
    
    List<Consulta> buscarPorGestanteId(Long gestanteId);
    
    /**
     * Busca consultas agendadas de um médico em uma data e horário específicos
     */
    List<Consulta> buscarConsultasAgendadas(Long medicoId, LocalDate data, LocalTime horario);
    
    /**
     * Busca todas as consultas agendadas de um médico em uma data
     */
    List<Consulta> buscarConsultasAgendadasPorMedicoEData(Long medicoId, LocalDate data);

    /**
     * Indica se existe ao menos uma consulta com status AGENDADA para o médico.
     */
    boolean existeAgendamentoPorMedico(Long medicoId);
}
