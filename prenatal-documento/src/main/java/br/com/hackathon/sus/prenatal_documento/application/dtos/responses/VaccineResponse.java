package br.com.hackathon.sus.prenatal_documento.application.dtos.responses;

import br.com.hackathon.sus.prenatal_documento.domain.models.Vaccine;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public record VaccineResponse(
        @JsonProperty("id") UUID id,
        @JsonProperty("gestanteCpf") String patientCpf,
        @JsonProperty("tipoVacina") String vaccineType,
        @JsonProperty("dataAplicacao") LocalDate applicationDate
) {
    public static VaccineResponse from(Vaccine vaccine) {
        return new VaccineResponse(
                vaccine.getId(),
                vaccine.getPatientCpf(),
                vaccine.getVaccineType(),
                vaccine.getApplicationDate()
        );
    }
}
