package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CreateDoctorScheduleRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorGateway;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;

public class CreateDoctorScheduleUseCaseImp implements CreateDoctorScheduleUseCase {

    private final DoctorScheduleGateway doctorScheduleGateway;
    private final DoctorGateway doctorGateway;

    public CreateDoctorScheduleUseCaseImp(DoctorScheduleGateway doctorScheduleGateway, DoctorGateway doctorGateway) {
        this.doctorScheduleGateway = doctorScheduleGateway;
        this.doctorGateway = doctorGateway;
    }

    @Override
    public DoctorSchedule execute(CreateDoctorScheduleRequest request) {
        Long medicoId = doctorGateway.buscarPorCrm(request.crm())
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado para o CRM informado: " + request.crm()));

        DoctorSchedule schedule = new DoctorSchedule(
                medicoId,
                request.unitId(),
                request.weekdays(),
                request.startTime(),
                request.endTime(),
                request.appointmentDurationMinutes()
        );

        if (doctorScheduleGateway.buscarPorMedicoId(medicoId).isPresent()) {
            throw new IllegalArgumentException("Já existe uma agenda cadastrada para este médico");
        }

        return doctorScheduleGateway.salvar(schedule);
    }
}
