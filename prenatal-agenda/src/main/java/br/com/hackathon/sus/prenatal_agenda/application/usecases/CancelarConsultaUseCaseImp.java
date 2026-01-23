package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.MotivoCancelamento;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.ConsultaGateway;

public class CancelarConsultaUseCaseImp implements CancelarConsultaUseCase {
    
    private final ConsultaGateway consultaGateway;
    
    public CancelarConsultaUseCaseImp(ConsultaGateway consultaGateway) {
        this.consultaGateway = consultaGateway;
    }
    
    @Override
    public Consulta execute(Long consultaId, MotivoCancelamento motivo) {
        Consulta consulta = consultaGateway.buscarPorId(consultaId)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));
        
        consulta.cancelar(motivo);
        
        return consultaGateway.salvar(consulta);
    }
}
