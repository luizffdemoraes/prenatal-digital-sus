package com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prontuario_historico", schema = "prontuario")
public class MedicalRecordHistoryEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "prontuario_id", nullable = false, columnDefinition = "uuid")
    private UUID medicalRecordId;

    @Column(name = "data", nullable = false)
    private LocalDateTime occurredAt;

    @Column(name = "profissional_user_id", nullable = false)
    private String professionalUserId;

    @Column(name = "alteracao", nullable = false, columnDefinition = "TEXT")
    private String description;

    public MedicalRecordHistoryEntity() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getMedicalRecordId() { return medicalRecordId; }
    public void setMedicalRecordId(UUID medicalRecordId) { this.medicalRecordId = medicalRecordId; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
    public String getProfessionalUserId() { return professionalUserId; }
    public void setProfessionalUserId(String professionalUserId) { this.professionalUserId = professionalUserId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
