package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.UpdateDoctorScheduleRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;

public class UpdateDoctorScheduleUseCaseImp implements UpdateDoctorScheduleUseCase {

    private final DoctorScheduleGateway doctorScheduleGateway;
    private final DoctorGateway doctorGateway;

    public UpdateDoctorScheduleUseCaseImp(DoctorScheduleGateway doctorScheduleGateway, DoctorGateway doctorGateway) {
        this.doctorScheduleGateway = doctorScheduleGateway;
        this.doctorGateway = doctorGateway;
    }

    @Override
    public DoctorSchedule execute(String crm, UpdateDoctorScheduleRequest request) {
        Long medicoId = doctorGateway.buscarPorCrm(crm)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado para o CRM informado: " + crm));

        DoctorSchedule existente = doctorScheduleGateway.buscarPorMedicoId(medicoId)
                .orElseThrow(() -> new IllegalArgumentException("Agenda não encontrada para este médico"));

        DoctorSchedule atualizada = new DoctorSchedule(
                existente.getId(),
                medicoId,
                request.unitId(),
                request.weekdays(),
                request.startTime(),
                request.endTime(),
                request.appointmentDurationMinutes()
        );

        return doctorScheduleGateway.salvar(atualizada);
    }
}
