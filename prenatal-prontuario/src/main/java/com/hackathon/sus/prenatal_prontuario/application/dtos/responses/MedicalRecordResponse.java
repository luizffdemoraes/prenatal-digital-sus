package com.hackathon.sus.prenatal_prontuario.application.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackathon.sus.prenatal_prontuario.domain.entities.DeliveryType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.MedicalRecord;
import com.hackathon.sus.prenatal_prontuario.domain.entities.PregnancyType;
import com.hackathon.sus.prenatal_prontuario.domain.entities.RiskFactor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Resposta do prontuário. Chaves JSON em português.
 */
public record MedicalRecordResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("cpf") String cpf,
        @JsonProperty("nomeCompleto") String fullName,
        @JsonProperty("dataNascimento") LocalDate dateOfBirth,
        @JsonProperty("gestanteId") UUID pregnantWomanId,
        @JsonProperty("consultaId") UUID appointmentId,
        @JsonProperty("dataUltimaMenstruacao") LocalDate lastMenstrualPeriod,
        @JsonProperty("idadeGestacionalSemanas") Integer gestationalAgeWeeks,
        @JsonProperty("tipoGestacao") PregnancyType pregnancyType,
        @JsonProperty("numeroGestacoesAnteriores") Integer previousPregnancies,
        @JsonProperty("numeroPartos") Integer previousDeliveries,
        @JsonProperty("numeroAbortos") Integer previousAbortions,
        @JsonProperty("gestacaoAltoRisco") Boolean highRiskPregnancy,
        @JsonProperty("motivoAltoRisco") String highRiskReason,
        @JsonProperty("fatoresRisco") List<RiskFactor> riskFactors,
        @JsonProperty("usoVitaminas") Boolean vitaminUse,
        @JsonProperty("usoAAS") Boolean aspirinUse,
        @JsonProperty("observacoes") String notes,
        @JsonProperty("tipoParto") DeliveryType deliveryType,
        @JsonProperty("criadoEm") LocalDateTime createdAt
) {
    /** Cria a resposta a partir do domínio (evita erros de resolução do construtor no mapper). */
    public static MedicalRecordResponse from(MedicalRecord m) {
        if (m == null) return null;
        return new MedicalRecordResponse(
                m.getId(),
                m.getCpf(),
                m.getFullName(),
                m.getDateOfBirth(),
                m.getPregnantWomanId(),
                m.getAppointmentId(),
                m.getLastMenstrualPeriod(),
                m.getGestationalAgeWeeks(),
                m.getPregnancyType(),
                m.getPreviousPregnancies(),
                m.getPreviousDeliveries(),
                m.getPreviousAbortions(),
                m.getHighRiskPregnancy(),
                m.getHighRiskReason(),
                m.getRiskFactors() != null ? new ArrayList<>(m.getRiskFactors()) : new ArrayList<>(),
                m.getVitaminUse(),
                m.getAspirinUse(),
                m.getNotes(),
                m.getDeliveryType(),
                m.getCreatedAt()
        );
    }
}
