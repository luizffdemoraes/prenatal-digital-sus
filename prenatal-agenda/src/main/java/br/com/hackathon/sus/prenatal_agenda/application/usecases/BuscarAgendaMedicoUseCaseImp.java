package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;

import java.util.Optional;

public class BuscarAgendaMedicoUseCaseImp implements BuscarAgendaMedicoUseCase {
    
    private final AgendaMedicoGateway agendaMedicoGateway;
    
    public BuscarAgendaMedicoUseCaseImp(AgendaMedicoGateway agendaMedicoGateway) {
        this.agendaMedicoGateway = agendaMedicoGateway;
    }
    
    @Override
    public Optional<AgendaMedico> execute(Long medicoId) {
        return agendaMedicoGateway.buscarPorMedicoId(medicoId);
    }
}
