package br.com.hackathon.sus.prenatal_agenda.application.dtos.responses;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalTime;
import java.util.Set;

/**
 * Java: English. JSON: @JsonProperty links to Portuguese API keys.
 */
public record DoctorScheduleResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("medicoId") Long doctorId,
        @JsonProperty("unidadeId") Long unitId,
        @JsonProperty("diasAtendimento") Set<Weekday> weekdays,
        @JsonProperty("horarioInicio") LocalTime startTime,
        @JsonProperty("horarioFim") LocalTime endTime,
        @JsonProperty("duracaoConsultaMinutos") Integer appointmentDurationMinutes
) {
}
