package br.com.hackathon.sus.prenatal_ia.infrastructure.gateways.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AppointmentApiResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("data") LocalDate data,
        @JsonProperty("horario") LocalTime horario,
        @JsonProperty("status") String status
) {}
