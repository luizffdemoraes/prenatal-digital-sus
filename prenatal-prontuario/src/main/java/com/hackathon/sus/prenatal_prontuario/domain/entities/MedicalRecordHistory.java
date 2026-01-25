package com.hackathon.sus.prenatal_prontuario.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Medical record change entry (history). MVP: date, professional userId, description.
 */
public class MedicalRecordHistory {

    private UUID id;
    private UUID medicalRecordId;
    private LocalDateTime occurredAt;
    private String professionalUserId;
    private String description;

    public MedicalRecordHistory(UUID medicalRecordId, String professionalUserId, String description) {
        this.medicalRecordId = medicalRecordId;
        this.professionalUserId = professionalUserId;
        this.description = description != null ? description : "";
        this.occurredAt = LocalDateTime.now();
    }

    public MedicalRecordHistory(UUID id, UUID medicalRecordId, LocalDateTime occurredAt,
                                String professionalUserId, String description) {
        this.id = id;
        this.medicalRecordId = medicalRecordId;
        this.occurredAt = occurredAt != null ? occurredAt : LocalDateTime.now();
        this.professionalUserId = professionalUserId != null ? professionalUserId : "sistema";
        this.description = description != null ? description : "";
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getMedicalRecordId() { return medicalRecordId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public String getProfessionalUserId() { return professionalUserId; }
    public String getDescription() { return description; }
}
