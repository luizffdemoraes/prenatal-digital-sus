package br.com.hackathon.sus.prenatal_agenda.application.dtos.responses;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.AppointmentStatus;
import br.com.hackathon.sus.prenatal_agenda.domain.entities.CancellationReason;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Java: English. JSON: @JsonProperty links to Portuguese API keys.
 * Retorna nome da paciente, nome do médico e especialidade (informação relevante).
 */
public record AppointmentResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("gestanteNome") String patientName,
        @JsonProperty("medicoNome") String doctorName,
        @JsonProperty("especialidade") String specialty,
        @JsonProperty("unidadeId") Long unitId,
        @JsonProperty("data") LocalDate date,
        @JsonProperty("horario") LocalTime time,
        @JsonProperty("status") AppointmentStatus status,
        @JsonProperty("motivoCancelamento") CancellationReason cancellationReason,
        @JsonProperty("dataAgendamento") LocalDateTime scheduledAt,
        @JsonProperty("dataCancelamento") LocalDateTime cancelledAt
) {
}
