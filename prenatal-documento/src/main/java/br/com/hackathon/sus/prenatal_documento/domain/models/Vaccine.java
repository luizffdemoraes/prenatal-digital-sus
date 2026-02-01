package br.com.hackathon.sus.prenatal_documento.domain.models;

import java.time.LocalDate;
import java.util.UUID;

public class Vaccine {
    private UUID id;
    private String patientCpf;
    private String vaccineType;
    private LocalDate applicationDate;

    public Vaccine() {
    }

    public Vaccine(String patientCpf, String vaccineType, LocalDate applicationDate) {
        this.patientCpf = patientCpf;
        this.vaccineType = vaccineType;
        this.applicationDate = applicationDate;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPatientCpf() { return patientCpf; }
    public void setPatientCpf(String patientCpf) { this.patientCpf = patientCpf; }
    public String getVaccineType() { return vaccineType; }
    public void setVaccineType(String vaccineType) { this.vaccineType = vaccineType; }
    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
}
