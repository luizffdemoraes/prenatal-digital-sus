package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.MotivoCancelamento;

public interface CancelarConsultaUseCase {
    Consulta execute(Long consultaId, MotivoCancelamento motivo);
}
