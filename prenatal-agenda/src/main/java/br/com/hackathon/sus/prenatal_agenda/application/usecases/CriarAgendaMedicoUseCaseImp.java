package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AgendaMedicoGateway;

public class CriarAgendaMedicoUseCaseImp implements CriarAgendaMedicoUseCase {
    
    private final AgendaMedicoGateway agendaMedicoGateway;
    
    public CriarAgendaMedicoUseCaseImp(AgendaMedicoGateway agendaMedicoGateway) {
        this.agendaMedicoGateway = agendaMedicoGateway;
    }
    
    @Override
    public AgendaMedico execute(AgendaMedico agenda) {
        // Verifica se já existe agenda para o médico
        var agendaExistente = agendaMedicoGateway.buscarPorMedicoId(agenda.getMedicoId());
        if (agendaExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe uma agenda cadastrada para este médico");
        }
        
        return agendaMedicoGateway.salvar(agenda);
    }
}
