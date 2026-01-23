package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AgendaMedico;

import java.util.Optional;

public interface BuscarAgendaMedicoUseCase {
    Optional<AgendaMedico> execute(Long medicoId);
}
