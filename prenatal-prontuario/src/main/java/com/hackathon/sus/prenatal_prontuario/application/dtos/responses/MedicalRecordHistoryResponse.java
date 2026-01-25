package com.hackathon.sus.prenatal_prontuario.application.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Medical record history item. JSON: data, profissionalUserId, alteracao.
 */
public record MedicalRecordHistoryResponse(
        @JsonProperty("data") LocalDateTime occurredAt,
        @JsonProperty("profissionalUserId") String professionalUserId,
        @JsonProperty("alteracao") String description
) {}
