package br.com.hackathon.sus.prenatal_documento.domain.repositories;

import br.com.hackathon.sus.prenatal_documento.domain.models.Vaccine;

import java.util.List;

public interface VaccineRepository {
    Vaccine save(Vaccine vaccine);
    List<Vaccine> findByPatientCpf(String patientCpf);
}
