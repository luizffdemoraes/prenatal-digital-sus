package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.gateways.AppointmentGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;

public class DeleteDoctorScheduleUseCaseImp implements DeleteDoctorScheduleUseCase {

    private final DoctorScheduleGateway doctorScheduleGateway;
    private final AppointmentGateway appointmentGateway;
    private final DoctorGateway doctorGateway;

    public DeleteDoctorScheduleUseCaseImp(DoctorScheduleGateway doctorScheduleGateway,
                                          AppointmentGateway appointmentGateway,
                                          DoctorGateway doctorGateway) {
        this.doctorScheduleGateway = doctorScheduleGateway;
        this.appointmentGateway = appointmentGateway;
        this.doctorGateway = doctorGateway;
    }

    @Override
    public void execute(String crm) {
        Long medicoId = doctorGateway.buscarPorCrm(crm)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado para o CRM informado: " + crm));

        var schedule = doctorScheduleGateway.buscarPorMedicoId(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada para este médico"));

        if (appointmentGateway.existeAgendamentoPorMedico(medicoId)) {
            throw new IllegalStateException("Não é possível excluir a agenda: existem consultas agendadas para este médico.");
        }

        doctorScheduleGateway.excluirPorId(schedule.getId());
    }
}
