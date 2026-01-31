package br.com.hackathon.sus.prenatal_ia.application.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PrenatalAlertDTO(
        @JsonProperty("type") String type,
        @JsonProperty("severity") String severity,
        @JsonProperty("message") String message,
        @JsonProperty("target") String target
) {}
