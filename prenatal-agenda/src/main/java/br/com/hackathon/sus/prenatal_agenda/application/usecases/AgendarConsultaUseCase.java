package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.AgendarConsultaRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;

public interface AgendarConsultaUseCase {
    /**
     * Agenda uma consulta. Unidade (UBS) via header X-Unidade-Id.
     * Gestante: nome e CPF. Médico: nome OU especialidade OU CRM.
     */
    Consulta execute(AgendarConsultaRequest request, Long unidadeId);
}
