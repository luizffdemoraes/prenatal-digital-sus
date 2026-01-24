package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CriarAgendaMedicoRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.MedicoResolver;

public class CriarAgendaMedicoUseCaseImp implements CriarAgendaMedicoUseCase {

    private final AgendaMedicoGateway agendaMedicoGateway;
    private final MedicoResolver medicoResolver;

    public CriarAgendaMedicoUseCaseImp(AgendaMedicoGateway agendaMedicoGateway, MedicoResolver medicoResolver) {
        this.agendaMedicoGateway = agendaMedicoGateway;
        this.medicoResolver = medicoResolver;
    }

    @Override
    public AgendaMedico execute(CriarAgendaMedicoRequest request) {
        Long medicoId = medicoResolver.buscarPorCrm(request.crm())
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado para o CRM informado: " + request.crm()));

        AgendaMedico agenda = new AgendaMedico(
                medicoId,
                request.unidadeId(),
                request.diasAtendimento(),
                request.horarioInicio(),
                request.horarioFim(),
                request.duracaoConsultaMinutos()
        );

        var agendaExistente = agendaMedicoGateway.buscarPorMedicoId(medicoId);
        if (agendaExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma agenda cadastrada para este médico");
        }

        return agendaMedicoGateway.salvar(agenda);
    }
}
