package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;
import br.com.hackathon.sus.prenatal_agenda.domain.gateways.DoctorScheduleGateway;

import java.util.Optional;

public class FindDoctorScheduleUseCaseImp implements FindDoctorScheduleUseCase {

    private final DoctorScheduleGateway doctorScheduleGateway;

    public FindDoctorScheduleUseCaseImp(DoctorScheduleGateway doctorScheduleGateway) {
        this.doctorScheduleGateway = doctorScheduleGateway;
    }

    @Override
    public Optional<DoctorSchedule> execute(Long medicoId) {
        return doctorScheduleGateway.buscarPorMedicoId(medicoId);
    }
}
