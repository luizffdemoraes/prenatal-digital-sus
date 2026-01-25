package com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.repository;

import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.MedicalRecordHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicalRecordHistoryRepository extends JpaRepository<MedicalRecordHistoryEntity, UUID> {

    List<MedicalRecordHistoryEntity> findByMedicalRecordIdOrderByOccurredAtDesc(UUID medicalRecordId);
}
