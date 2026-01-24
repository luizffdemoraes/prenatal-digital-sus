package br.com.hackathon.sus.prenatal_agenda.application.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.util.List;

/**
 * Java: English. JSON: @JsonProperty links to Portuguese API keys.
 * Consulta por CRM; response com nome do médico e especialidade.
 */
public record AvailableTimeSlotResponse(
        @JsonProperty("medicoNome") String doctorName,
        @JsonProperty("especialidade") String specialty,
        @JsonProperty("data") String date,
        @JsonProperty("horariosDisponiveis") List<LocalTime> availableTimeSlots
) {
}
