package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.application.dtos.requests.UpdateDoctorScheduleRequest;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;

public interface UpdateDoctorScheduleUseCase {
    DoctorSchedule execute(String crm, UpdateDoctorScheduleRequest request);
}
