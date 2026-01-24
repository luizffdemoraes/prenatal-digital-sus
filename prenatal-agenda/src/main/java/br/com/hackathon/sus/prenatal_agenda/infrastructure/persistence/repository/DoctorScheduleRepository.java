package br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository;

import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.DoctorScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorScheduleRepository extends JpaRepository<DoctorScheduleEntity, Long> {

    Optional<DoctorScheduleEntity> findByMedicoId(Long medicoId);
}
