package br.com.hackathon.sus.prenatal_agenda.application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request to schedule an appointment (MVP).
 * Patient: name and CPF. Doctor: name OR specialty OR CRM. Unit: X-Unidade-Id header.
 * Java: English. JSON: @JsonProperty links to Portuguese API keys.
 */
public record CreateAppointmentRequest(
        @JsonProperty("gestanteNome")
        @NotBlank(message = "{appointment.patientName.required}")
        String patientName,

        @JsonProperty("gestanteCpf")
        @NotBlank(message = "{appointment.patientCpf.required}")
        String patientCpf,

        @JsonProperty("medicoNome")
        String doctorName,

        @JsonProperty("especialidade")
        String specialty,

        @JsonProperty("crm")
        String crm,

        @JsonProperty("data")
        @NotNull(message = "{appointment.date.required}")
        @FutureOrPresent(message = "{appointment.date.futureOrPresent}")
        LocalDate date,

        @JsonProperty("horario")
        @NotNull(message = "{appointment.time.required}")
        LocalTime time
) {
}
