package br.com.hackathon.sus.prenatal_ia.domain.entities;

import java.util.List;

public class PrenatalAnalysisResult {
    private String patientId;
    private String patientName;
    private String patientEmail;
    private Integer gestationalWeeks;
    private List<PrenatalAlert> alerts;
    private String doctorName;
    private String doctorEmail;

    public PrenatalAnalysisResult() {
    }

    public PrenatalAnalysisResult(String patientId, String patientName, String patientEmail, Integer gestationalWeeks, List<PrenatalAlert> alerts) {
        this(patientId, patientName, patientEmail, gestationalWeeks, alerts, null, null);
    }

    public PrenatalAnalysisResult(String patientId, String patientName, String patientEmail, Integer gestationalWeeks,
                                  List<PrenatalAlert> alerts, String doctorName, String doctorEmail) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.gestationalWeeks = gestationalWeeks;
        this.alerts = alerts;
        this.doctorName = doctorName;
        this.doctorEmail = doctorEmail;
    }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public String getPatientEmail() { return patientEmail; }
    public void setPatientEmail(String patientEmail) { this.patientEmail = patientEmail; }
    public Integer getGestationalWeeks() { return gestationalWeeks; }
    public void setGestationalWeeks(Integer gestationalWeeks) { this.gestationalWeeks = gestationalWeeks; }
    public List<PrenatalAlert> getAlerts() { return alerts; }
    public void setAlerts(List<PrenatalAlert> alerts) { this.alerts = alerts; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorEmail() { return doctorEmail; }
    public void setDoctorEmail(String doctorEmail) { this.doctorEmail = doctorEmail; }
}
