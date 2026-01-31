package br.com.hackathon.sus.prenatal_ia.domain.entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentSummary {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String status;

    public AppointmentSummary() {
    }

    public AppointmentSummary(Long id, LocalDate date, LocalTime time, String status) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
