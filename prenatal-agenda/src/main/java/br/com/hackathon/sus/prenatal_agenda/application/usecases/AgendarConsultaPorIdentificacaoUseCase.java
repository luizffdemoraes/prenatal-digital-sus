package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.AgendarConsultaRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;

public interface AgendarConsultaPorIdentificacaoUseCase {

    Consulta execute(AgendarConsultaRequest request);
}
