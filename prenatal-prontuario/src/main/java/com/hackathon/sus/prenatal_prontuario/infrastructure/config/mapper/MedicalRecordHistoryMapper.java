package com.hackathon.sus.prenatal_prontuario.infrastructure.config.mapper;

import com.hackathon.sus.prenatal_prontuario.application.dtos.responses.MedicalRecordHistoryResponse;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecordHistory;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.MedicalRecordHistoryEntity;

public final class MedicalRecordHistoryMapper {

    private MedicalRecordHistoryMapper() {}

    public static MedicalRecordHistory toDomain(MedicalRecordHistoryEntity e) {
        if (e == null) return null;
        return new MedicalRecordHistory(
                e.getId(),
                e.getMedicalRecordId(),
                e.getOccurredAt(),
                e.getProfessionalUserId(),
                e.getDescription()
        );
    }

    public static MedicalRecordHistoryResponse toResponse(MedicalRecordHistory h) {
        if (h == null) return null;
        return new MedicalRecordHistoryResponse(
                h.getOccurredAt(),
                h.getProfessionalUserId(),
                h.getDescription()
        );
    }

    public static MedicalRecordHistoryEntity fromDomain(MedicalRecordHistory h) {
        if (h == null) return null;
        MedicalRecordHistoryEntity e = new MedicalRecordHistoryEntity();
        e.setId(h.getId());
        e.setMedicalRecordId(h.getMedicalRecordId());
        e.setOccurredAt(h.getOccurredAt());
        e.setProfessionalUserId(h.getProfessionalUserId());
        e.setDescription(h.getDescription());
        return e;
    }
}
