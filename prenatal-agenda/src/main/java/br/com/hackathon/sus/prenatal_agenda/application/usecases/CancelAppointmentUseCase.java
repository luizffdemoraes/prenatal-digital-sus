package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.CancellationReason;

public interface CancelAppointmentUseCase {
    Appointment execute(Long appointmentId, CancellationReason reason);
}
