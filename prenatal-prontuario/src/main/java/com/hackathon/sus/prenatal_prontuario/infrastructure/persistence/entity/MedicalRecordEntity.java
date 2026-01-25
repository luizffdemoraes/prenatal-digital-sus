package com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity;

import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.RiskFactor;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.converter.DeliveryTypeConverter;
import com.hackathon.sus.prenatal_prontuario.infrastructure.persistence.entity.converter.PregnancyTypeConverter;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "prontuario", schema = "prontuario")
public class MedicalRecordEntity {

    @Id
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "cpf", length = 11)
    private String cpf;

    @Column(name = "nome_completo")
    private String fullName;

    @Column(name = "data_nascimento")
    private LocalDate dateOfBirth;

    @Column(name = "gestante_id", columnDefinition = "uuid")
    private UUID pregnantWomanId;

    @Column(name = "consulta_id", columnDefinition = "uuid")
    private UUID appointmentId;

    @Column(name = "data_ultima_menstruacao")
    private LocalDate lastMenstrualPeriod;

    @Column(name = "idade_gestacional_semanas")
    private Integer gestationalAgeWeeks;

    @Convert(converter = PregnancyTypeConverter.class)
    @Column(name = "tipo_gestacao", nullable = false)
    private PregnancyType pregnancyType;

    @Column(name = "numero_gestacoes_anteriores", nullable = false)
    private Integer previousPregnancies;

    @Column(name = "numero_partos", nullable = false)
    private Integer previousDeliveries;

    @Column(name = "numero_abortos", nullable = false)
    private Integer previousAbortions;

    @Column(name = "gestacao_alto_risco", nullable = false)
    private Boolean highRiskPregnancy;

    @Column(name = "motivo_alto_risco", columnDefinition = "TEXT")
    private String highRiskReason;

    @ElementCollection(targetClass = RiskFactor.class)
    @CollectionTable(name = "prontuario_fatores_risco", schema = "prontuario", joinColumns = @JoinColumn(name = "prontuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "fator_risco")
    private List<RiskFactor> riskFactors = new ArrayList<>();

    @Column(name = "uso_vitaminas", nullable = false)
    private Boolean vitaminUse;

    @Column(name = "uso_aas", nullable = false)
    private Boolean aspirinUse;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String notes;

    @Convert(converter = DeliveryTypeConverter.class)
    @Column(name = "tipo_parto")
    private DeliveryType deliveryType;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime createdAt;

    public MedicalRecordEntity() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public UUID getPregnantWomanId() { return pregnantWomanId; }
    public void setPregnantWomanId(UUID pregnantWomanId) { this.pregnantWomanId = pregnantWomanId; }
    public UUID getAppointmentId() { return appointmentId; }
    public void setAppointmentId(UUID appointmentId) { this.appointmentId = appointmentId; }
    public LocalDate getLastMenstrualPeriod() { return lastMenstrualPeriod; }
    public void setLastMenstrualPeriod(LocalDate lastMenstrualPeriod) { this.lastMenstrualPeriod = lastMenstrualPeriod; }
    public Integer getGestationalAgeWeeks() { return gestationalAgeWeeks; }
    public void setGestationalAgeWeeks(Integer gestationalAgeWeeks) { this.gestationalAgeWeeks = gestationalAgeWeeks; }
    public PregnancyType getPregnancyType() { return pregnancyType; }
    public void setPregnancyType(PregnancyType pregnancyType) { this.pregnancyType = pregnancyType; }
    public Integer getPreviousPregnancies() { return previousPregnancies; }
    public void setPreviousPregnancies(Integer previousPregnancies) { this.previousPregnancies = previousPregnancies; }
    public Integer getPreviousDeliveries() { return previousDeliveries; }
    public void setPreviousDeliveries(Integer previousDeliveries) { this.previousDeliveries = previousDeliveries; }
    public Integer getPreviousAbortions() { return previousAbortions; }
    public void setPreviousAbortions(Integer previousAbortions) { this.previousAbortions = previousAbortions; }
    public Boolean getHighRiskPregnancy() { return highRiskPregnancy; }
    public void setHighRiskPregnancy(Boolean highRiskPregnancy) { this.highRiskPregnancy = highRiskPregnancy; }
    public String getHighRiskReason() { return highRiskReason; }
    public void setHighRiskReason(String highRiskReason) { this.highRiskReason = highRiskReason; }
    public List<RiskFactor> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<RiskFactor> riskFactors) { this.riskFactors = riskFactors != null ? riskFactors : new ArrayList<>(); }
    public Boolean getVitaminUse() { return vitaminUse; }
    public void setVitaminUse(Boolean vitaminUse) { this.vitaminUse = vitaminUse; }
    public Boolean getAspirinUse() { return aspirinUse; }
    public void setAspirinUse(Boolean aspirinUse) { this.aspirinUse = aspirinUse; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public DeliveryType getDeliveryType() { return deliveryType; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
