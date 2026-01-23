package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.ConsultaGateway;

import java.util.List;

public class BuscarConsultasPorGestanteUseCaseImp implements BuscarConsultasPorGestanteUseCase {
    
    private final ConsultaGateway consultaGateway;
    
    public BuscarConsultasPorGestanteUseCaseImp(ConsultaGateway consultaGateway) {
        this.consultaGateway = consultaGateway;
    }
    
    @Override
    public List<Consulta> execute(Long gestanteId) {
        return consultaGateway.buscarPorGestanteId(gestanteId);
    }
}
