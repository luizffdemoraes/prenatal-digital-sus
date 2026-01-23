package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Consulta;

public interface AgendarConsultaUseCase {
    Consulta execute(Consulta consulta);
}
