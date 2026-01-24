package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CreateAppointmentRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;

public interface CreateAppointmentUseCase {
    Appointment execute(CreateAppointmentRequest request, Long unidadeId);
}
