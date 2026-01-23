package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;

public interface CriarAgendaMedicoUseCase {
    AgendaMedico execute(AgendaMedico agenda);
}
