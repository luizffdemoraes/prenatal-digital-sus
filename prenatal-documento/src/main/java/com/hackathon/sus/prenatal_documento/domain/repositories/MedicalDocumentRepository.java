package com.hackathon.sus.prenatal_documento.domain.repositories;

import com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicalDocumentRepository {
    MedicalDocument save(MedicalDocument document);
    Optional<MedicalDocument> findById(UUID id);
    List<MedicalDocument> findByPrenatalRecordIdAndActiveTrue(Long prenatalRecordId);
    void delete(MedicalDocument document);
}
