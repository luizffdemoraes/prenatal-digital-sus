package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.CreateDoctorScheduleRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;

public interface CreateDoctorScheduleUseCase {
    DoctorSchedule execute(CreateDoctorScheduleRequest request);
}
