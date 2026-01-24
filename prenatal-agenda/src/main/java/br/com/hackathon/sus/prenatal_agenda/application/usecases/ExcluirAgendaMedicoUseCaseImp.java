package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.ConsultaGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.MedicoResolver;

public class ExcluirAgendaMedicoUseCaseImp implements ExcluirAgendaMedicoUseCase {

    private final AgendaMedicoGateway agendaMedicoGateway;
    private final ConsultaGateway consultaGateway;
    private final MedicoResolver medicoResolver;

    public ExcluirAgendaMedicoUseCaseImp(AgendaMedicoGateway agendaMedicoGateway,
                                         ConsultaGateway consultaGateway,
                                         MedicoResolver medicoResolver) {
        this.agendaMedicoGateway = agendaMedicoGateway;
        this.consultaGateway = consultaGateway;
        this.medicoResolver = medicoResolver;
    }

    @Override
    public void execute(String crm) {
        Long medicoId = medicoResolver.buscarPorCrm(crm)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado para o CRM informado: " + crm));

        var agenda = agendaMedicoGateway.buscarPorMedicoId(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada para este médico"));

        if (consultaGateway.existeAgendamentoPorMedico(medicoId)) {
            throw new IllegalStateException("Não é possível excluir a agenda: existem consultas agendadas para este médico.");
        }

        agendaMedicoGateway.excluirPorId(agenda.getId());
    }
}
