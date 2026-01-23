package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DiaSemana;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.ConsultaGateway;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * UseCase para consultar disponibilidade de horários
 * Usa table-driven design para gerar slots de horários
 */
public class ConsultarDisponibilidadeUseCaseImp implements ConsultarDisponibilidadeUseCase {
    
    private final AgendaMedicoGateway agendaMedicoGateway;
    private final ConsultaGateway consultaGateway;
    
    public ConsultarDisponibilidadeUseCaseImp(AgendaMedicoGateway agendaMedicoGateway,
                                            ConsultaGateway consultaGateway) {
        this.agendaMedicoGateway = agendaMedicoGateway;
        this.consultaGateway = consultaGateway;
    }
    
    @Override
    public List<LocalTime> execute(Long medicoId, LocalDate data) {
        // Busca a agenda do médico
        AgendaMedico agenda = agendaMedicoGateway.buscarPorMedicoId(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada para o médico informado"));
        
        // Verifica se o médico atende no dia da semana
        DiaSemana diaSemana = DiaSemana.fromDayOfWeek(data.getDayOfWeek());
        if (!agenda.atendeNoDia(diaSemana)) {
            return List.of(); // Retorna lista vazia se não atende no dia
        }
        
        // Gera todos os slots possíveis usando table-driven approach
        List<LocalTime> todosOsSlots = gerarSlotsDisponiveis(agenda);
        
        // Busca consultas já agendadas para o médico na data
        List<Consulta> consultasAgendadas = consultaGateway.buscarConsultasAgendadasPorMedicoEData(medicoId, data);
        
        // Cria um Set de horários ocupados para lookup rápido (table-driven)
        Set<LocalTime> horariosOcupados = consultasAgendadas.stream()
                .filter(Consulta::estaAgendada)
                .map(Consulta::getHorario)
                .collect(Collectors.toSet());
        
        // Filtra os slots disponíveis removendo os ocupados
        return todosOsSlots.stream()
                .filter(horario -> !horariosOcupados.contains(horario))
                .toList();
    }
    
    /**
     * Gera todos os slots de horários possíveis baseado na agenda
     * Usa table-driven design: itera sobre o período calculando os slots
     */
    private List<LocalTime> gerarSlotsDisponiveis(AgendaMedico agenda) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime horarioAtual = agenda.getHorarioInicio();
        LocalTime horarioFim = agenda.getHorarioFim();
        int duracaoMinutos = agenda.getDuracaoConsultaMinutos();
        
        // Tabela de regras: enquanto o horário atual + duração não ultrapassar o fim
        while (!horarioAtual.plusMinutes(duracaoMinutos).isAfter(horarioFim)) {
            slots.add(horarioAtual);
            horarioAtual = horarioAtual.plusMinutes(duracaoMinutos);
        }
        
        return slots;
    }
}
