package br.com.hackathon.sus.prenatal_agenda.application.usecases;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.DoctorSchedule;

import java.util.Optional;

public interface FindDoctorScheduleUseCase {
    Optional<DoctorSchedule> execute(Long medicoId);
}
