package br.com.hackathon.sus.prenatal_ia.domain.entities;

import java.time.LocalDate;

public class VaccineRecord {
    private String type;
    private LocalDate date;

    public VaccineRecord() {
    }

    public VaccineRecord(String type, LocalDate date) {
        this.type = type;
        this.date = date;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
