package br.com.hackathon.sus.prenatal_documento.application.usecases;

import br.com.hackathon.sus.prenatal_documento.domain.models.Vaccine;

public interface RegisterVaccineUseCase {
    Vaccine execute(String patientCpf, String vaccineType, java.time.LocalDate applicationDate);
}
