package br.com.hackathon.sus.prenatal_agenda.application.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.util.List;

/**
 * Java: English. JSON: @JsonProperty links to Portuguese API keys.
 */
public record AvailableTimeSlotResponse(
        @JsonProperty("medicoId") Long doctorId,
        @JsonProperty("data") String date,
        @JsonProperty("horariosDisponiveis") List<LocalTime> availableTimeSlots
) {
}
