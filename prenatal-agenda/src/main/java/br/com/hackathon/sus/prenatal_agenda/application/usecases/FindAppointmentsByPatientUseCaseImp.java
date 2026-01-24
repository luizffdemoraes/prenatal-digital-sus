package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Appointment;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;

import java.util.List;

public class FindAppointmentsByPatientUseCaseImp implements FindAppointmentsByPatientUseCase {

    private final AppointmentGateway appointmentGateway;

    public FindAppointmentsByPatientUseCaseImp(AppointmentGateway appointmentGateway) {
        this.appointmentGateway = appointmentGateway;
    }

    @Override
    public List<Appointment> execute(Long gestanteId) {
        return appointmentGateway.buscarPorGestanteId(gestanteId);
    }
}
