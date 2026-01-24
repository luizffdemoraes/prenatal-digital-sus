package br.com.hackathon.sus.prenatal_agenda.application.dtos.responses;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AppointmentStatus;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.CancellationReason;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Java: English. JSON: @JsonProperty links to Portuguese API keys.
 */
public record AppointmentResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("gestanteId") Long patientId,
        @JsonProperty("medicoId") Long doctorId,
        @JsonProperty("unidadeId") Long unitId,
        @JsonProperty("data") LocalDate date,
        @JsonProperty("horario") LocalTime time,
        @JsonProperty("status") AppointmentStatus status,
        @JsonProperty("motivoCancelamento") CancellationReason cancellationReason,
        @JsonProperty("dataAgendamento") LocalDateTime scheduledAt,
        @JsonProperty("dataCancelamento") LocalDateTime cancelledAt
) {
}
