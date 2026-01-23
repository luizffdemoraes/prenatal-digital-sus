package br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.repository;

import br.com.hackathon.sus.prenatal_agenda.infrastructure.persistence.entity.AgendaMedicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgendaMedicoRepository extends JpaRepository<AgendaMedicoEntity, Long> {
    
    Optional<AgendaMedicoEntity> findByMedicoId(Long medicoId);
}
