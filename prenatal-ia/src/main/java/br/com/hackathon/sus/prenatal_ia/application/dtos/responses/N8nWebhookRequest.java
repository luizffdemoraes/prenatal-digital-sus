package br.com.hackathon.sus.prenatal_ia.application.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record N8nWebhookRequest(
        @JsonProperty("patientId") String patientId,
        @JsonProperty("patientName") String patientName,
        @JsonProperty("patientEmail") String patientEmail,
        @JsonProperty("gestationalWeeks") Integer gestationalWeeks,
        @JsonProperty("alerts") List<PrenatalAlertDTO> alerts
) {}
