package com.hackathon.sus.prenatal_documento.infrastructure.persistence.repositories;

import com.hackathon.sus.prenatal_documento.infrastructure.persistence.entities.MedicalDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaMedicalDocumentRepository extends JpaRepository<MedicalDocumentEntity, UUID> {
    List<MedicalDocumentEntity> findByPrenatalRecordIdAndActiveTrue(Long prenatalRecordId);
}
