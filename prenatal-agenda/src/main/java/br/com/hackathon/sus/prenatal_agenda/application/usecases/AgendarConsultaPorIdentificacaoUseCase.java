package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.AgendarConsultaRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;

public interface AgendarConsultaPorIdentificacaoUseCase {

    /**
     * @param unidadeId id da UBS que está realizando o agendamento (vem do header X-Unidade-Id)
     */
    Consulta execute(AgendarConsultaRequest request, Long unidadeId);
}
