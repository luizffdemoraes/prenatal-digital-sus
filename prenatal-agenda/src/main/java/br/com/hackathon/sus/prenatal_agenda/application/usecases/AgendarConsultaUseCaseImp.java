package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import java.util.List;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DiaSemana;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.ConsultaGateway;

public class AgendarConsultaUseCaseImp implements AgendarConsultaUseCase {
    
    private final ConsultaGateway consultaGateway;
    private final AgendaMedicoGateway agendaMedicoGateway;
    
    public AgendarConsultaUseCaseImp(ConsultaGateway consultaGateway,
                                    AgendaMedicoGateway agendaMedicoGateway) {
        this.consultaGateway = consultaGateway;
        this.agendaMedicoGateway = agendaMedicoGateway;
    }
    
    @Override
    public Consulta execute(Consulta consulta) {
        // Busca a agenda do médico
        var agenda = agendaMedicoGateway.buscarPorMedicoId(consulta.getMedicoId())
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada para o médico informado"));
        
        // Valida se o médico atende no dia da semana da consulta
        DiaSemana diaSemana = DiaSemana.fromDayOfWeek(consulta.getData().getDayOfWeek());
        if (!agenda.atendeNoDia(diaSemana)) {
            throw new IllegalArgumentException("Médico não atende neste dia da semana");
        }
        
        // Valida se o horário está dentro do período de atendimento
        if (!agenda.horarioDentroDoPeriodo(consulta.getHorario())) {
            throw new IllegalArgumentException("Horário fora do período de atendimento do médico");
        }
        
        // Verifica se já existe consulta agendada no mesmo horário
        List<Consulta> consultasExistentes = consultaGateway.buscarConsultasAgendadas(
                consulta.getMedicoId(),
                consulta.getData(),
                consulta.getHorario()
        );
        
        if (!consultasExistentes.isEmpty()) {
            throw new IllegalArgumentException("Horário já está ocupado");
        }
        
        return consultaGateway.salvar(consulta);
    }
}
