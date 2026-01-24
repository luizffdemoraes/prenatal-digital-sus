package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;

import java.util.List;

public interface FindAppointmentsByPatientUseCase {
    List<Appointment> execute(Long gestanteId);
}
