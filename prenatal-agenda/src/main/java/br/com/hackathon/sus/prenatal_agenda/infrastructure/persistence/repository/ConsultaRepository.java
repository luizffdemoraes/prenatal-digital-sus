package br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.StatusConsulta;
import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.ConsultaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<ConsultaEntity, Long> {
    
    List<ConsultaEntity> findByGestanteId(Long gestanteId);
    
    List<ConsultaEntity> findByMedicoIdAndDataAndHorarioAndStatus(
            Long medicoId, LocalDate data, LocalTime horario, StatusConsulta status);
    
    List<ConsultaEntity> findByMedicoIdAndDataAndStatus(
            Long medicoId, LocalDate data, StatusConsulta status);

    boolean existsByMedicoIdAndStatus(Long medicoId, StatusConsulta status);
}
