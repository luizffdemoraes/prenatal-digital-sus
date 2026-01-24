package br.com.hackathon.sus.prenatal_agenda.application.dtos.requests;

import br.com.hackathon.sus.prenatal_agenda.domain.entities.Weekday;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalTime;
import java.util.Set;

/**
 * Java: English. JSON: @JsonProperty links to Portuguese API keys.
 */
public record UpdateDoctorScheduleRequest(
        @JsonProperty("unidadeId")
        @NotNull(message = "{doctorSchedule.unitId.required}")
        @Positive(message = "{doctorSchedule.unitId.positive}")
        Long unitId,

        @JsonProperty("diasAtendimento")
        @NotNull(message = "{doctorSchedule.weekdays.required}")
        Set<Weekday> weekdays,

        @JsonProperty("horarioInicio")
        @NotNull(message = "{doctorSchedule.startTime.required}")
        LocalTime startTime,

        @JsonProperty("horarioFim")
        @NotNull(message = "{doctorSchedule.endTime.required}")
        LocalTime endTime,

        @JsonProperty("duracaoConsultaMinutos")
        @NotNull(message = "{doctorSchedule.appointmentDurationMinutes.required}")
        @Positive(message = "{doctorSchedule.appointmentDurationMinutes.positive}")
        Integer appointmentDurationMinutes
) {
}
