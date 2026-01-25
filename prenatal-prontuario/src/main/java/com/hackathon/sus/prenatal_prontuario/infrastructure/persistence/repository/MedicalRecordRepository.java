package com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.repository;

import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.MedicalRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, UUID> {

    Optional<MedicalRecordEntity> findByPregnantWomanId(UUID pregnantWomanId);

    Optional<MedicalRecordEntity> findByCpf(String cpf);

    boolean existsByPregnantWomanId(UUID pregnantWomanId);

    boolean existsByCpf(String cpf);
}
