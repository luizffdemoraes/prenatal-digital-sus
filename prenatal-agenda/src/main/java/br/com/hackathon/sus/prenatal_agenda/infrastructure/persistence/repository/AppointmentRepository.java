package br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AppointmentStatus;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    List<AppointmentEntity> findByGestanteId(Long gestanteId);

    List<AppointmentEntity> findByMedicoIdAndDataAndHorarioAndStatus(
            Long medicoId, LocalDate data, LocalTime horario, AppointmentStatus status);

    List<AppointmentEntity> findByMedicoIdAndDataAndStatus(
            Long medicoId, LocalDate data, AppointmentStatus status);

    boolean existsByMedicoIdAndStatus(Long medicoId, AppointmentStatus status);
}
