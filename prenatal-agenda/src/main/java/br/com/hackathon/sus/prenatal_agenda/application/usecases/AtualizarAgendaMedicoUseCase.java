package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.AtualizarAgendaMedicoRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;

public interface AtualizarAgendaMedicoUseCase {
    AgendaMedico execute(String crm, AtualizarAgendaMedicoRequest request);
}
