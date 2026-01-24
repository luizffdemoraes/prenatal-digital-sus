package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.AtualizarAgendaMedicoRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.MedicoResolver;

public class AtualizarAgendaMedicoUseCaseImp implements AtualizarAgendaMedicoUseCase {

    private final AgendaMedicoGateway agendaMedicoGateway;
    private final MedicoResolver medicoResolver;

    public AtualizarAgendaMedicoUseCaseImp(AgendaMedicoGateway agendaMedicoGateway, MedicoResolver medicoResolver) {
        this.agendaMedicoGateway = agendaMedicoGateway;
        this.medicoResolver = medicoResolver;
    }

    @Override
    public AgendaMedico execute(String crm, AtualizarAgendaMedicoRequest request) {
        Long medicoId = medicoResolver.buscarPorCrm(crm)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado para o CRM informado: " + crm));

        AgendaMedico existente = agendaMedicoGateway.buscarPorMedicoId(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada para este médico"));

        AgendaMedico atualizada = new AgendaMedico(
                existente.getId(),
                medicoId,
                request.unidadeId(),
                request.diasAtendimento(),
                request.horarioInicio(),
                request.horarioFim(),
                request.duracaoConsultaMinutos()
        );

        return agendaMedicoGateway.salvar(atualizada);
    }
}
