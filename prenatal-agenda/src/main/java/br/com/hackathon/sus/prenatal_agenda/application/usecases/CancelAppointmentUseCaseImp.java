package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.CancellationReason;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;

public class CancelAppointmentUseCaseImp implements CancelAppointmentUseCase {

    private final AppointmentGateway appointmentGateway;

    public CancelAppointmentUseCaseImp(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public Appointment execute(Long appointmentId, CancellationReason reason) {
        Appointment appointment = appointmentGateway.buscarPorId(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada"));

        appointment.cancelar(reason);

        return appointmentGateway.salvar(appointment);
    }
}
