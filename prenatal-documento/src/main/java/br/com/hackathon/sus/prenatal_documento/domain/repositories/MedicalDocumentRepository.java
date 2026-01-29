package br.com.hackathon.sus.prenatal_documento.domain.repositories;

import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicalDocumentRepository {
    MedicalDocument save(MedicalDocument document);
    Optional<MedicalDocument> findById(UUID id);
    List<MedicalDocument> findByPatientCpfAndActiveTrue(String patientCpf);
    void delete(MedicalDocument document);
}
