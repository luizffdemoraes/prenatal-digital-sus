package br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.repositories;

import br.com.hackathon.sus.prenatal_documento.domain.models.MedicalDocument;
import br.com.hackathon.sus.prenatal_documento.domain.repositories.MedicalDocumentRepository;
import br.com.hackathon.sus.prenatal_documento.infrastructure.persistence.entities.MedicalDocumentEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class MedicalDocumentRepositoryImpl implements MedicalDocumentRepository {

    private final JpaMedicalDocumentRepository jpaRepository;

    public MedicalDocumentRepositoryImpl(JpaMedicalDocumentRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MedicalDocument save(MedicalDocument document) {
        MedicalDocumentEntity entity = toEntity(document);
        if (document.getId() != null) {
            entity.setId(document.getId());
        }
        MedicalDocumentEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<MedicalDocument> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<MedicalDocument> findByPatientCpfAndActiveTrue(String patientCpf) {
        return jpaRepository.findByPatientCpfAndActiveTrue(patientCpf)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(MedicalDocument document) {
        MedicalDocumentEntity entity = toEntity(document);
        jpaRepository.delete(entity);
    }

    private MedicalDocumentEntity toEntity(MedicalDocument domain) {
        MedicalDocumentEntity entity = new MedicalDocumentEntity();
        entity.setId(domain.getId());
        entity.setPatientCpf(domain.getPatientCpf());
        entity.setFileName(domain.getFileName());
        entity.setOriginalFileName(domain.getOriginalFileName());
        entity.setContentType(domain.getContentType());
        entity.setFileSize(domain.getFileSize());
        entity.setDocumentType(domain.getDocumentType());
        entity.setStoragePath(domain.getStoragePath());
        entity.setActive(domain.getActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setDeletedAt(domain.getDeletedAt());
        return entity;
    }

    private MedicalDocument toDomain(MedicalDocumentEntity entity) {
        MedicalDocument domain = new MedicalDocument();
        domain.setId(entity.getId());
        domain.setPatientCpf(entity.getPatientCpf());
        domain.setFileName(entity.getFileName());
        domain.setOriginalFileName(entity.getOriginalFileName());
        domain.setContentType(entity.getContentType());
        domain.setFileSize(entity.getFileSize());
        domain.setDocumentType(entity.getDocumentType());
        domain.setStoragePath(entity.getStoragePath());
        domain.setActive(entity.getActive());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setUpdatedAt(entity.getUpdatedAt());
        domain.setDeletedAt(entity.getDeletedAt());
        return domain;
    }
}
